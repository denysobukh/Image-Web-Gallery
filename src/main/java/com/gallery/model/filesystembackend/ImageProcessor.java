package com.gallery.model.filesystembackend;

import com.gallery.application.GalleryException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * ImageProcessor class
 * keeps track of filesystem changes
 * updates DB if needed
 * updates thumbnails if needed
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 18:57 [Friday]
 */
@Component
public class ImageProcessor {
    /**
     * number of CPUs to be used as maximum number of threads
     */
    private static final int CPUS_NUMBER = Runtime.getRuntime().availableProcessors();

    /**
     * timeout for all asynchronous waits
     */
    private static final int TIMEOUT_SECONDS = 15;

    private final ExecutorService executorService = Executors.newWorkStealingPool();
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private DirectoryWalker directoryWalker;
    @Autowired
    private FileRepository filesDB;
    @Autowired
    private Logger logger;

    @PostConstruct
    private void postConstruct() {
        logger.info(CPUS_NUMBER + " cpu(s) found");
    }

    @PreDestroy
    private void preDestroy() {
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
            executorService.shutdownNow();
        } catch (InterruptedException e) {
            logger.warn("Could not wait until workers terminated", e);
        }
    }

    /**
     * Asynchronously compares files found on disk with DB records, deletes thumbnails of images
     * which were not found in DB
     * creates DB records for files which are found for the first time
     * creates thumbnails for them
     *
     * @throws GalleryException
     */
    public synchronized void updateRepository() {
        logger.info("Repository update stared");

        Future<Set<ImageFile>> futureDiskFiles = executorService.submit(this::getDiskAll);
        Future<Set<ImageFile>> futureDbFiles = executorService.submit(this::getDbAll);

        Future<Difference> futureDifference = executorService.submit(() -> {
            Set<ImageFile> dbFiles = futureDbFiles.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Set<ImageFile> diskFiles = futureDiskFiles.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Difference d = new Difference(diskFiles, dbFiles);
            logger.debug("Calculated " + d);
            return null;
        });

        executorService.submit(() -> {
            Set<ImageFile> removed = futureDifference.get(TIMEOUT_SECONDS, TimeUnit.SECONDS).removedFiles;
            if (removed != null) {
                executorService.submit(() -> this.deleteThumbnails(removed));
                executorService.submit(() -> this.deleteDb(removed));
            }
            return null;
        });

        executorService.submit(() -> {
            Set<ImageFile> newFiles = futureDifference.get(TIMEOUT_SECONDS, TimeUnit.SECONDS).newFiles;
            if (newFiles.size() > 0) {
                final BlockingQueue<ImageFile> updateFilesQueue = new LinkedBlockingQueue<>(newFiles);

                IntStream.range(0, CPUS_NUMBER).forEach(i -> {
                    executorService.submit(() -> updateThumbnail(updateFilesQueue));
                });
            }
            return null;
        });
    }

    private void updateThumbnail(BlockingQueue<ImageFile> filesQueue) {
        logger.trace("updateThumbnail started");

        ImageFile file;
        int c = 0;
        try {
            while ((file = filesQueue.poll(TIMEOUT_SECONDS, TimeUnit.SECONDS)) != null) {
                // TODO: 2019-04-25 generate thumbnail
                file.setThumbnailPath(file.getSourcePath());
                filesDB.save(file);
                c++;
            }
            logger.info(c + " thumbs updated, exiting");
        } catch (InterruptedException e) {
            logger.info("Interrupted while waitning for updateFilesQueue", e);
        }
    }

    private Set<ImageFile> getDbAll() {
        logger.trace("getDbAll started");
        Set<ImageFile> files = StreamSupport.stream(filesDB.findAll().spliterator(), true)
                .collect(Collectors.toCollection(HashSet::new));
        logger.debug("getDbAll found " + files.size());
        return files;
    }

    private Set<ImageFile> getDiskAll() {
        logger.trace("getDiskAll started");
        Set<ImageFile> diskFiles = null;
        try {
            diskFiles =
                    directoryWalker.listFilesDeep(
                            directoryWalker.getRoot())
                            .stream()
                            .map(p -> directoryWalker.getRoot().resolve(p))
                            .map(sourcePath -> {
                                try {
                                    return ImageFile.build(sourcePath);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toCollection(HashSet::new));
            logger.debug("getDiskAll found " + diskFiles.size());
        } catch (DirectoryWalkerException e) {
            logger.error("Cant list files on disk", e);
        }
        return diskFiles;
    }

    private void deleteDb(Set<ImageFile> files) {
        logger.trace("deleteDb started");
        filesDB.deleteAll(files);
        logger.debug("deleteDb removedFiles " + files.size());
    }

    private void deleteThumbnails(Set<ImageFile> files) {
        logger.trace("deleteThumbnails started");
        int deleted = 0, skipped = 0;
        for (ImageFile file : files) {
            String source = file.getSourcePath();
            String thumbnail = file.getThumbnailPath();
            if (thumbnail != null) {
                if (thumbnail.equals(source)) {
                    logger.trace("Skipping " + thumbnail);
                    skipped++;
                }
            } else {
                // TODO: 2019-04-25 real delete
                logger.trace("Deleting " + thumbnail);
                deleted++;
            }
        }
        logger.debug("deleteThumbnails skipped " + skipped + " removedFiles " + deleted);
    }

    void copyFile(String inFile, String outFile) throws IOException {
        FileChannel sourceChannel = new FileInputStream(inFile).getChannel();
        FileChannel destChannel = new FileOutputStream(outFile).getChannel();
        try {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (IOException e) {
            throw e;
        } finally {
            sourceChannel.close();
            destChannel.close();
        }
    }

    private static class Difference {
        final Set<ImageFile> removedFiles;
        final Set<ImageFile> newFiles;

        /**
         * @param diskFiles files founded in the filesystem
         * @param dbFiles   files previously diskFiles in DB
         */
        Difference(Set<ImageFile> diskFiles, Set<ImageFile> dbFiles) {
            // get list of removed files
            removedFiles = new HashSet<>(dbFiles);
            removedFiles.removeAll(diskFiles);

            // get list of newFiles files
            newFiles = new HashSet<>(diskFiles);
            newFiles.removeAll(dbFiles);
        }

        @Override
        public String toString() {
            return String.format(
                    "Diff [ removedFiles: %s; newFiles: %s  ]", removedFiles.size(), newFiles.size());
        }
    }


}
