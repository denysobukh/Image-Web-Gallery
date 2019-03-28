package com.gallery.model.directory;

import com.gallery.model.ImageExtension;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DirectoryWalker implements functionality of {@code DirectoryWalkerI}
 * <p>
 * 2019-03-21 16:43 [Thursday]
 *
 * @author Dennis Obukhov
 */
@Component("Walker")
@DependsOn("Logger")
public class DirectoryWalker implements DirectoryWalkerI {

    @Autowired
    private Logger logger;

    private final Path rootDir;

    @Override
    public boolean isRootDir(Path p) {
        return p.compareTo(rootDir) != 0;
    }

    @Override
    public boolean isWithinRootDir(Path p) {
        p = rootDir.resolve(p);
        p = p.normalize();
        return Files.isDirectory(p) && p.startsWith(rootDir);
    }

    /**
     * Constructs DirectoryWalker with the given path and sets it as the root directory
     *
     * @param path to the root directory
     * @throws DirectoryWalkerException if the specified path is not a directory
     */
    @Autowired
    public DirectoryWalker(@Qualifier("rootDir") Path path) throws DirectoryWalkerException {
        // makes defencive copy
        Path copy = Paths.get(path.toAbsolutePath().toString());
        if (!Files.isDirectory(copy)) throw new DirectoryWalkerException("Is not a directory", copy);
        rootDir = copy;
    }

    @PostConstruct
    private void postConstruct() {
        logger.debug("Constructed with root = " + rootDir.toAbsolutePath());
    }

    @Override
    public Path getParent(Path path) throws DirectoryWalkerException {
        if (!this.isWithinRootDir(path))
            throw new DirectoryWalkerException("Path is out of the root or invalid", path);
        return path.compareTo(rootDir) == 0 ?
                null :
                rootDir.relativize(path.getParent());
    }

    @Override
    public List<Path> listDirs(Path path) throws DirectoryWalkerException {
        if (!this.isWithinRootDir(path))
            throw new DirectoryWalkerException("Path is out of the root or invalid", path);
        try {
            return Files.walk(path, 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isDirectory)
                    .filter(p -> p.compareTo(path) != 0)
                    .map(p -> rootDir.relativize(p.normalize()))
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new DirectoryWalkerException(e);
        }
    }

    @Override
    public List<Path> listFiles(Path path) throws DirectoryWalkerException {
        if (!this.isWithinRootDir(path))
            throw new DirectoryWalkerException("Is out of the root or invalid", path);
        try {
            return Files.walk(path, 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isRegularFile)
                    /*
                     it's assumed that the file's extension comes after the last dot
                     it is extracted and test it against valid image extensions
                     if it's not valid it is omitted
                    */
                    .filter(f -> {
                        String name = f.getFileName().toString();
                        int i = name.lastIndexOf(".") + 1;
                        return ImageExtension.test(name.substring(i));
                    })
                    .map(p -> rootDir.relativize(p.normalize()))
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new DirectoryWalkerException(e);
        }
    }

    @Override
    public List<Path> listAll(Path path) throws DirectoryWalkerException {
        List<Path> list = listDirs(path);
        list.addAll(listFiles(path));
        return list;
    }

    @Override
    public Path getRoot() {
        return rootDir;
    }

    @Override
    public boolean isRoot(Path path) {
        return rootDir.compareTo(path.normalize()) == 0;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(\"" + rootDir + "\")";
    }
}
