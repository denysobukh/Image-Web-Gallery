package com.gallery.model.file;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * ThumbnailsManager class
 * keeps track of filesystem changes
 * updates DB if needed
 * updates thumbnails if needed
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 18:57 [Friday]
 */
@Component
public class ThumbnailsManager {
    /**
     * number of CPUs to be used as maximum number of threads
     */
    private static final int CPUS_NUMBER = Runtime.getRuntime().availableProcessors();

    /**
     * timeout for all asynchronous waits
     */
    private static final int TIMEOUT_SECONDS = 15;

    private final ExecutorService executorService = Executors.newWorkStealingPool();
    private Semaphore dbLock = new Semaphore(1);
    private Semaphore diskLock = new Semaphore(1);
    @Autowired
    private DirectoryManager directoryManager;
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
     * creates their thumbnails if needed
     */
    public void updateRepository() {
        logger.info("Repository update stared");

        Future<Set<ImageFile>> futureDiskFiles = executorService.submit(() -> {
            diskLock.acquire();
            return getAllDisk();
        });

        Future<Set<ImageFile>> futureDbFiles = executorService.submit(() -> {
            dbLock.acquire();
            return getAllDB();
        });

        Future<Difference> futureDifference = executorService.submit(() -> {
            Set<ImageFile> dbFiles = futureDbFiles.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Set<ImageFile> diskFiles = futureDiskFiles.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            Difference d = new Difference(diskFiles, dbFiles);
            logger.debug("Calculated " + d);
            return d;
        });

        executorService.submit(() -> {
            try {
                Set<ImageFile> removed = futureDifference.get(TIMEOUT_SECONDS, TimeUnit.SECONDS).removedFiles;
                if (removed.size() > 0) {
                    executorService.submit(() -> this.deleteThumbnails(removed));
                    executorService.submit(() -> this.deleteDb(removed));
                } else {
                    logger.debug("0 files were removed, nothing to do, exiting");
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.warn("Can not wait for the result", e);
            }
        });

        executorService.submit(() -> {
            try {
                Set<ImageFile> newFiles = futureDifference.get(TIMEOUT_SECONDS, TimeUnit.SECONDS).newFiles;
                if (newFiles.size() > 0) {
                    final BlockingQueue<ImageFile> source = new LinkedBlockingQueue<>(newFiles);

                    IntStream.range(0, CPUS_NUMBER).forEach(i -> {
                        executorService.submit(() -> updateThumbnail(source));
                    });
                } else {
                    logger.debug("0 files were updated, nothing to do, exiting");
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.warn("Can not wait for the result", e);
            }
            dbLock.release();
            diskLock.release();
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
            logger.info("updateThumbnail updated {} files , exiting", c);
        } catch (InterruptedException e) {
            logger.info("Interrupted while waitning for updateFilesQueue", e);
        }
    }

    private Set<ImageFile> getAllDB() {
        logger.trace("getAllDB started");
        Set<ImageFile> files = StreamSupport.stream(filesDB.findAll().spliterator(), true)
                .collect(Collectors.toCollection(HashSet::new));
        logger.debug("getAllDB found " + files.size());
        return files;
    }

    private Set<ImageFile> getAllDisk() {
        logger.trace("getAllDisk started");
        Set<ImageFile> diskFiles = new HashSet<>();
        try {
            diskFiles =
                    directoryManager.listFilesDeep(
                            directoryManager.getRoot())
                            .stream()
                            .map(p -> directoryManager.getRoot().resolve(p))
                            .map(ImageFile::build)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toCollection(HashSet::new));
            logger.debug("getAllDisk found " + diskFiles.size());
        } catch (DirectoryManagerException e) {
            logger.error("Cant list files on disk", e);
        }
        return diskFiles;
    }

    private void deleteDb(Set<ImageFile> files) {
        logger.trace("deleteDb started");
        filesDB.deleteAll(files);
        logger.debug("deleteDb removed: " + files.size());
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
        logger.debug("deleteThumbnails skipped: " + skipped + " removed: " + deleted);
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

    public void cleanUpRepository() {
        executorService.submit(() -> {
            try {
                dbLock.acquire();
                diskLock.acquire();

                Set<ImageFile> all = getAllDB();
                executorService.submit(() -> deleteDb(all));
                executorService.submit(() -> deleteThumbnails(all));

                logger.info("Deleted {} files", all.size());
            } catch (InterruptedException e) {
                logger.warn("Can not acquire lock", e);
            } finally {
                dbLock.release();
                diskLock.release();
            }
        });
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

            // get list of new files
            newFiles = new HashSet<>(diskFiles);
            newFiles.removeAll(dbFiles);
        }

        @Override
        public String toString() {
            return String.format(
                    "Diff [ -%s; +%s]", removedFiles.size(), newFiles.size());
        }
    }


}
