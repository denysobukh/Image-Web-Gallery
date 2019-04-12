package com.gallery.model.directory;

import com.gallery.application.GalleryException;
import com.gallery.model.file.File;
import com.gallery.model.file.FileRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
    DirectoryWalker directoryWalker;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    Logger logger;


    public Set<File> scan() throws GalleryException {
        Set<File> filesExists =
                directoryWalker.listFilesDeep(
                        directoryWalker.getRoot())
                        .stream()
                        .map(p -> new File(p.toString(), null))
                        .collect(Collectors.toCollection(HashSet::new));

        Set<File> filesStored =
                StreamSupport.stream(fileRepository.findAll().spliterator(), false)
                        .collect(Collectors.toCollection(HashSet::new));

        Set<File> filesDeleted = new HashSet<>();
        filesDeleted.addAll(filesStored);
        filesDeleted.removeAll(filesExists);

        Set<File> newFiles = new HashSet<>();
        newFiles.addAll(filesExists);
        newFiles.removeAll(filesStored);

        System.out.println("filesExists: " + filesExists.size());
        System.out.println("filesStored: " + filesStored.size());
        System.out.println("filesDeleted: " + filesDeleted.size());

        fileRepository.deleteAll(filesDeleted);
        fileRepository.saveAll(newFiles);

        return filesExists;
    }


}
