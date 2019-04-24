package com.gallery.model.filesystembackend;

import com.gallery.application.GalleryException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * ImageProcessor class
 * keeps DB state consistent with Storage state
 * updates DB if needed
 * resize new images to thumbnails
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 18:57 [Friday]
 */
@Component
public class ImageProcessor {
    @Autowired
    private DirectoryWalker directoryWalker;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private Logger logger;

    @PersistenceContext
    EntityManager entityManager;


    public Result scan() throws GalleryException {

        logger.info("Scan started");

        Set<ImageFile> filesInStorage =
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

        Set<ImageFile> filesInRepository =
                StreamSupport.stream(fileRepository.findAll().spliterator(), false)
                        .collect(Collectors.toCollection(HashSet::new));

        int repositoryCount = filesInRepository.size();
        int storageCount = filesInStorage.size();

        // get list of non-exist files
        Set<ImageFile> filesDeleted = new HashSet<>(filesInRepository);
        filesDeleted.removeAll(filesInStorage);
        int deletedCount = filesDeleted.size();
        // delete them from DB
        fileRepository.deleteAll(filesDeleted);
        filesInRepository.removeAll(filesDeleted);


        Set<ImageFile> filesAdded = new HashSet<>(filesInStorage);
        filesAdded.removeAll(filesInRepository);
        int addedCount = filesAdded.size();

        //!TODO processing
        fileRepository.saveAll(filesAdded);


        if (filesAdded.size() > 0) {
            ImageFile f = (ImageFile) filesAdded.toArray()[0];
            logger.debug(f.toString());
            logger.debug("entityManager.contains() " + entityManager.contains(f));
            f.setThumbnailPath(f.getThumbnailPath() + "1");
            fileRepository.save(f);
        }

        Result result = new Result(storageCount, repositoryCount, deletedCount,
                addedCount);
        logger.info(result.toString());
        return result;

    }

    public static class Result {
        public final int storageCount;
        public final int repositoryCount;
        public final int deletedCount;
        public final int addedCount;

        /**
         * @param storageCount    number of files founded in the filesystem
         * @param repositoryCount number of files previously stored in DB
         * @param deletedCount    number of files which where removed from the filesystem since the previous run
         * @param addedCount      number of files which where added to the filesystem since the previous run
         */
        public Result(int storageCount, int repositoryCount, int deletedCount, int addedCount) {
            this.storageCount = storageCount;
            this.repositoryCount = repositoryCount;
            this.deletedCount = deletedCount;
            this.addedCount = addedCount;
        }

        @Override
        public String toString() {
            return String.format(
                    "Result [ storageCount: %s; repositoryCount: %s; addedCount: %s; deletedCount: %s  ]",
                    storageCount, repositoryCount, addedCount, deletedCount);
        }
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


}
