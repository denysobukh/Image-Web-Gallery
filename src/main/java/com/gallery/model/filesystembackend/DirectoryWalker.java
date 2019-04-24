package com.gallery.model.filesystembackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DirectoryWalker finds files and directories
 * <p>
 * 2019-03-21 16:43 [Thursday]
 *
 * @author Dennis Obukhov
 */
@Component("Walker")
public class DirectoryWalker {

    private final Path rootDir;
    @Autowired
    private Logger logger;


    /**
     * Constructs DirectoryWalker with the given path and sets it as the root directory
     *
     * @param path to the root directory
     * @throws DirectoryWalkerException if the specified path is not a directory
     */
    @Autowired
    public DirectoryWalker(@Qualifier("rootDir") Path path) throws DirectoryWalkerException {
        LoggerFactory.getLogger(this.getClass()).debug("Constructor path = " + path);
        rootDir = path.toAbsolutePath();
        if (!Files.isDirectory(rootDir)) throw new DirectoryWalkerException("Is not a directory", rootDir);
    }

    /**
     * Tests if the given path is within root
     *
     * @param p given Path
     * @return {@true} if the given path is some child paths of the root
     */
    public boolean withinRoot(Path p) {
        p = rootDir.resolve(p);
        p = p.normalize();
        return Files.isDirectory(p) && p.startsWith(rootDir);
    }

    @PostConstruct
    private void postConstruct() {
        logger.debug("DirectoryWalker set root = " + rootDir.toAbsolutePath());
    }

    /**
     * Returns Path of parent directory or null if the given directory is the Root directory
     *
     * @param path the given path
     * @return Path of parent directory
     * @throws DirectoryWalkerException if the given path is invalid or filesystem error occurred
     */
    public Path getParent(Path path) throws DirectoryWalkerException {
        if (!withinRoot(path))
            throw new DirectoryWalkerException("Path is out of the root or invalid", path);
        return path.compareTo(rootDir) == 0 ?
                null :
                rootDir.relativize(path.getParent());
    }

    /**
     * Returns the list of directories at the given Path or an empty list
     *
     * @param path the give path
     * @return the list of directories in the given Path or an empty list
     * @throws DirectoryWalkerException if the given path is invalid or filesystem error occurred
     */
    public List<Path> listDirs(Path path) throws DirectoryWalkerException {
        if (!withinRoot(path))
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

    /**
     * Returns the list of files in the given directory of an empty list
     *
     * @param path the given path
     * @return the list of files in the given Path of empty list
     * @throws DirectoryWalkerException if the given path is invalid or filesystem error occurred
     */
    public List<Path> listFiles(Path path) throws DirectoryWalkerException {
        return listFiles(path, 1, FileTypeFilter.IMAGE);
    }

    /**
     * Returns the list of files in the given directory and all of it subdirectories
     *
     * @param path the given path
     * @return the list of files in the given Path of empty list
     * @throws DirectoryWalkerException if the given path is invalid or filesystem error occurred
     */
    public List<Path> listFilesDeep(Path path) throws DirectoryWalkerException {
        return listFiles(path, Integer.MAX_VALUE, FileTypeFilter.IMAGE);
    }

    private List<Path> listFiles(Path path, int maxDepth, FileTypeFilterI filter) throws DirectoryWalkerException {
        if (!withinRoot(path))
            throw new DirectoryWalkerException("Is out of the root or invalid", path);
        try {
            return Files.walk(path, maxDepth, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isRegularFile)
                    /*
                     it's assumed that the file's extension comes after the last dot
                     it is extracted and isTheRootDir it against valid image extensions
                     if it's not valid it is omitted
                    */
                    .filter(f -> {
                        String name = f.getFileName().toString();
                        int i = name.lastIndexOf(".") + 1;
                        return filter.isA(name.substring(i));
                    })
                    .map(p -> rootDir.relativize(p.normalize()))
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new DirectoryWalkerException(e);
        }
    }


    /**
     * Returns the list of files along with directories in the given path
     *
     * @param path the given path
     * @return the list of files along with directories in the given path of empty list if
     * @throws DirectoryWalkerException if the given path is invalid or filesystem error occurred
     */
    public List<Path> listAll(Path path) throws DirectoryWalkerException {
        List<Path> list = listDirs(path);
        list.addAll(listFiles(path));
        return list;
    }

    /**
     * Returns the root directory Path
     *
     * @return the root directory Path
     */
    public Path getRoot() {
        return rootDir;
    }

    /**
     * Returns {@code true} if the given directory is the root directory
     *
     * @param path the given path
     * @return {@code true} if the given directory is the root directory
     */
    public boolean isRoot(Path path) {
        return rootDir.compareTo(path.normalize()) == 0;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "(\"" + rootDir + "\")";
    }
}
