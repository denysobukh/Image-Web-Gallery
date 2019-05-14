package com.gallery.service;

import com.gallery.application.BackgroundExecutor;
import com.gallery.model.image.Image;
import com.gallery.model.image.ImageCrudRepository;
import com.gallery.model.directory.DirectoryRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
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
     * timeout for all asynchronous waits
     */
    private static final int TIMEOUT_SECONDS = 15;

    @Autowired
    private BackgroundExecutor executorService;
    private Semaphore dbLock = new Semaphore(1);
    private Semaphore diskLock = new Semaphore(1);
    @Autowired
    private Disk disk;
    @Autowired
    private ImageCrudRepository filesDB;
    @Autowired
    private DirectoryRepository directoryRepository;
    @Autowired
    private Logger logger;

    /**
     * Asynchronously compares files found on disk with DB records, deletes thumbnails of images
     * which were not found in DB
     * creates DB records for files which are found for the first time
     * creates their thumbnails if needed
     */
    public void updateRepository() {
        logger.info("Repository update stared");

        Future<Set<Image>> futureDbFiles = executorService.submit(() -> {
            dbLock.acquire();
            return getAllFilesDB();
        });

        Future<Difference<Image>> futureDifference = executorService.submit(() -> {
            Set<Image> dbFiles = futureDbFiles.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            // FIXME: 2019-05-01
//            Difference d = new Difference(directoryTreeBuilder.getFiles(), dbFiles);
//            logger.debug("Calculated " + d);
//            logger.trace(directoryTreeBuilder.toString());
//            return d;
            return null;
        });

        executorService.submit(() -> {
            try {
                Set<Image> removed = futureDifference.get(TIMEOUT_SECONDS, TimeUnit.SECONDS).getRemoved();
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
                Set<Image> newFiles = futureDifference.get(TIMEOUT_SECONDS, TimeUnit.SECONDS).getAdded();
                if (newFiles.size() > 0) {
                    final BlockingQueue<Image> source = new LinkedBlockingQueue<>(newFiles);
//                    TODO: 2019-05-02
//                    IntStream.range(0, CPUS_NUMBER).forEach(i -> {
//                        executorService.submit(() -> updateThumbnail(source));
//                    });
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


    private void updateThumbnail(BlockingQueue<Image> filesQueue) {
        logger.trace("updateThumbnail started");

        Image file;
        int c = 0;
        try {
            while ((file = filesQueue.poll(TIMEOUT_SECONDS, TimeUnit.SECONDS)) != null) {
                // TODO: 2019-04-25 generate thumbnail
                // file.setThumbnail(file.calculateThumbnail());
                filesDB.save(file);
                c++;
            }
            logger.info("updateThumbnail updated {} files , exiting", c);
        } catch (InterruptedException e) {
            logger.info("Interrupted while waitning for updateFilesQueue", e);
        }
    }

    private Set<Image> getAllFilesDB() {
        logger.trace("getAllFilesDB started");
        Set<Image> files = StreamSupport.stream(filesDB.findAll().spliterator(), true)
                .collect(Collectors.toCollection(HashSet::new));
        logger.debug("getAllFilesDB found " + files.size());
        return files;
    }

    private Set<Image> getAllFilesDisk() {
        logger.trace("getAllFilesDisk started");
        Set<Image> diskFiles = new HashSet<>();
        try {
            diskFiles =
                    disk.listAllFiles()
                            .stream()
                            .map(p -> disk.getRoot().resolve(p).toString())
                            .map(Image::new)
                            .collect(Collectors.toCollection(HashSet::new));
            logger.debug("getAllFilesDisk found " + diskFiles.size());
        } catch (DiskException e) {
            logger.error("Cant list files on disk", e);
        }
        return diskFiles;
    }

    private void deleteDb(Set<Image> files) {
        logger.trace("deleteDb started");
        filesDB.deleteAll(files);
        logger.debug("deleteDb removed: " + files.size());
    }

    private void deleteThumbnails(Set<Image> files) {
        logger.trace("deleteThumbnails started");
        int deleted = 0, skipped = 0;
        for (Image file : files) {
            String source = file.getSource();
            String thumbnail = file.getThumbnail();
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
        try (FileChannel sourceChannel = new FileInputStream(inFile).getChannel();
             FileChannel destChannel = new FileOutputStream(outFile).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }

    public void cleanUpRepository() {
        executorService.submit(() -> {
            try {
                dbLock.acquire();
                diskLock.acquire();

                Set<Image> all = getAllFilesDB();
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
}
