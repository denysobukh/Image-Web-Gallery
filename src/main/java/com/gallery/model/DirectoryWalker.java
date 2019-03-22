package com.gallery.model;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DirectoryWalker implements functionality of DirectoryWalkerI
 * <p>
 * 2019-03-21 16:43 [Thursday]
 *
 * @author Dennis Obukhov
 */
@Component("Walker")
@Scope("prototype")
@DependsOn("Logger")
public class DirectoryWalker implements DirectoryWalkerI {

    @Autowired
    private Logger logger;

    private final Path rootDir;
    private Path previousDir;
    private Path currentDir;

    /**
     * Constructs DirectoryWalker and sets root and current directory to specified path
     *
     * @param rootPath path to the root directory
     * @throws DirectoryWalkerException if the specified path is not a directory
     */
    public DirectoryWalker(@Value("${gallery.filesystem.root}") String rootPath) throws DirectoryWalkerException {
        rootDir = Paths.get(rootPath);
        if (rootDir == null || !Files.isDirectory(rootDir)) throw new DirectoryWalkerException("Is not a directory",
                rootDir);
        currentDir = rootDir;
        logger.debug("Constructed with root = " + rootDir.toAbsolutePath());
    }

    @Override
    public Path getCurrent() {
        return currentDir;
    }

    @Override
    public List<Path> listDirs() throws DirectoryWalkerException {
        try {
            return Files.walk(currentDir, 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isDirectory)
                    .filter((p -> {
                        return p.compareTo(currentDir) != 0;
                    }))
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new DirectoryWalkerException(e);
        }
    }

    @Override
    public List<Path> listFiles() throws DirectoryWalkerException {
        try {
            return Files.walk(currentDir, 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isRegularFile)

                    /*
                     it's assumed that the file's extension comes after the last dot
                     it is extracted and test it against valid image extensions
                     if it's not valid it is omitted
                    */
                    .filter(f -> {
                        String name = f.getFileName().toString();
                        int i = name.lastIndexOf(".") + 1;
                        return FileType.validate(name.substring(i));
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new DirectoryWalkerException(e);
        }
    }

    @Override
    public List<Path> listAll() throws DirectoryWalkerException {
        List<Path> list = listDirs();
        list.addAll(listFiles());
        return list;
    }

    @Override
    public void enter(Path path) throws DirectoryWalkerException {
        Path walker = clone(path);
        while ((walker != null) && (walker.compareTo(rootDir) != 0))
            walker = walker.getParent();
        if (walker == null) throw new DirectoryWalkerException("Given path is outside the root path", path);
        previousDir = currentDir;
        currentDir = clone(path);
    }

    @Override
    public void back() throws DirectoryWalkerException {
        if (previousDir == null) throw new DirectoryWalkerException("Has not entered any directory so far");
    }

    @Override
    public Path getRoot() {
        return rootDir;
    }

    @Override
    public boolean isRoot() {
        return currentDir.compareTo(rootDir) == 0;
    }

    private static Path clone(Path p) {
        return Paths.get(p.toAbsolutePath().toString());
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[root = " + rootDir + "]";
    }
}
