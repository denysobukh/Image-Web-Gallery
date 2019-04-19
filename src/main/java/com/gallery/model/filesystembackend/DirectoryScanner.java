package com.gallery.model.filesystembackend;

import com.gallery.application.GalleryException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * DirectoryScanner class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 18:57 [Friday]
 */
@Component
public class DirectoryScanner {
    @Autowired
    private DirectoryWalker directoryWalker;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private Logger logger;


    public Set<ImageFile> scan() throws GalleryException {

//        List<Path> filesExists = directoryWalker.listFilesDeep(directoryWalker.getRoot());
//        Iterable<ImageFile> persistedFiles = fileRepository.findAll();

        Set<ImageFile> filesExists =
                directoryWalker.listFilesDeep(
                        directoryWalker.getRoot())
                        .stream()
                        .map(p -> directoryWalker.getRoot().resolve(p))
                        .map(ImageFile::build)
                        .collect(Collectors.toCollection(HashSet::new));

        Set<ImageFile> filesStored =
                StreamSupport.stream(fileRepository.findAll().spliterator(), false)
                        .collect(Collectors.toCollection(HashSet::new));

        Set<ImageFile> filesDeleted = new HashSet<>();
        filesDeleted.addAll(filesStored);
        filesDeleted.removeAll(filesExists);

        Set<ImageFile> newGalleryImages = new HashSet<>();
        newGalleryImages.addAll(filesExists);
        newGalleryImages.removeAll(filesStored);

        int cExists = filesExists.size(), cStored = filesStored.size(), cDeleted = filesDeleted.size();
        logger.info(String.format("Scanner starting [ filesystem: %s; database: %s; removed: %s ]", cExists, cStored,
                cDeleted));

        fileRepository.deleteAll(filesDeleted);
        fileRepository.saveAll(newGalleryImages);
/*

        File f = new File("cache");
        try (FileWriter fileWriter = new FileWriter(f, true)) {
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write("tt");
            writer.close();
        } catch (IOException e) {
            logger.warn("Scanner: ", e);
        }
*/

        return filesExists;
    }


}
