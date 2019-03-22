package com.gallery.model;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilesystemImageStorage implements ImageStorage {

    @Autowired
    private Logger logger;

    private final String rootFolder;

    @Autowired
    FilesystemImageStorage(@Value("${gallery.filesystem.root}") String rootFolder) {
        this.rootFolder = rootFolder;
    }

    @Override
    public List<Path> getAll() throws IOException {
        Path rootPath = Paths.get(rootFolder);
        if (!Files.isDirectory(rootPath)) throw new IllegalArgumentException(rootFolder + " is not a folder");

        return walkFolder(rootPath);
    }

    private List<Path> walkFolder(Path path) throws IOException {

        List<Path> files =
                Files.walk(path, 1, FileVisitOption.FOLLOW_LINKS)
                        .filter(Files::isRegularFile)
                        .filter(f -> {
                            String name = f.getFileName().toString();
                            int i = name.lastIndexOf(".") + 1;
                            return FileType.validate(name.substring(i));
                        })
                        .collect(Collectors.toCollection(LinkedList::new));


        List<Path> folders =
                Files.walk(path, 1, FileVisitOption.FOLLOW_LINKS)
                        .filter(Files::isDirectory)
                        .filter((p -> {
                            return p.compareTo(path) != 0;
                        }))
                        .collect(Collectors.toCollection(LinkedList::new));

        folders.forEach((f) -> {
            try {
                files.addAll(walkFolder(f));
            } catch (IOException e) {
                logger.error("Error while walking folder " + f, e);
            }
        });

        return files;
    }

    public String getRootFolder() {
        return rootFolder;
    }
}
