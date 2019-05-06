package com.gallery.model;

import com.gallery.model.directory.Directory;
import com.gallery.model.image.ExtensionFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Disk finds files and directories
 * <p>
 * 2019-03-21 16:43 [Thursday]
 *
 * @author Dennis Obukhov
 */
public class Disk {

    private final Path rootPath;
    private final Map<String, Directory> directories = new HashMap<>();
    @Autowired
    private Logger logger;
    private volatile Directory rootDirectory;

    /**
     * Constructs Disk with the given path and sets it as the rootDirectory directory
     *
     * @param directory is the rootDirectory directory
     * @throws DiskException if the specified path is not a directory
     */
    @Autowired
    public Disk(String directory) throws DiskException {
        LoggerFactory.getLogger(this.getClass()).debug("Constructed with path: " + directory);
        rootPath = Paths.get(directory).toAbsolutePath();
        if (!Files.isDirectory(rootPath)) throw new DiskException("Is not a directory", rootPath);
    }

    /**
     * Tests if the given path is within rootDirectory
     *
     * @param p given Path
     * @return {@code true} if the given path is some child paths of the rootDirectory
     */
    public boolean isOutOfRoot(Path p) {
        p = rootPath.resolve(p);
        p = p.normalize();
        return !Files.isDirectory(p) || !p.startsWith(rootPath);
    }

    @PostConstruct
    private void postConstruct() {
        logger.debug("Disk set rootDirectory = " + rootPath.toAbsolutePath());
    }

    /**
     * Returns the list of all directories recursively from the root
     *
     * @return the list of directories
     * @throws DiskException if filesystem error occurred
     */
    private List<Path> readAllDirs() throws DiskException {
        try {
            return Files.walk(rootPath, Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isDirectory)
                    .filter(p -> p.compareTo(rootPath) != 0)
                    .collect(Collectors.toCollection(LinkedList::new));
        } catch (IOException e) {
            throw new DiskException(e);
        }
    }

    /**
     * Returns the list of files in the given directory and all of it subdirectories
     *
     * @return the list of files in the given Path of empty list
     * @throws DiskException if the given path is invalid or filesystem error occurred
     */
    public List<Path> listAllFiles() throws DiskException {
        return listFiles(rootPath, Integer.MAX_VALUE);
    }

    public List<Path> listFiles(Path path, int maxDepth) throws DiskException {
        if (isOutOfRoot(path))
            throw new DiskException("Is out of the rootDirectory or invalid", path);
        try {
            return Files.walk(path, maxDepth, FileVisitOption.FOLLOW_LINKS)
                    .filter(f -> ExtensionFileFilter.IMAGE.accept(f.toFile()))
                    .map(p -> rootPath.relativize(p.normalize()))
                    .collect(Collectors.toCollection(LinkedList::new));
        } catch (IOException e) {
            throw new DiskException(e);
        }
    }

    /**
     * Returns the rootDirectory directory Path
     *
     * @return the rootDirectory directory Path
     */
    public Path getRoot() {
        return rootPath;
    }

    public Directory getTreeRoot() throws DiskException {
        return buildFromRoot();
    }

    /**
     * Builds Directory directories hierarchy
     *
     * @return root Directory node
     * @throws DiskException if filesystem error occurred
     */
    private Directory buildFromRoot() throws DiskException {
        Directory root = this.rootDirectory;
        if (root == null) {
            synchronized (this) {
                root = this.rootDirectory;
                if (root == null) {
                    root = addToTree(rootPath);
                    root.setRoot(true);
                    this.rootDirectory = root;
                    for (Path path : readAllDirs()) {
                        addToTree(path);
                    }
                }
            }
        }
        return root;
    }

    private Directory addToTree(Path path) {
        String source = path.toString();
        Directory directory = directories.get(source);
        if (directory == null) {
            String name = path.getFileName().toString();
            directory = new Directory(name, source);
            directory.setImagesCount(getImagesCount(path));
            directories.put(source, directory);
        }

        Directory parent = directories.get(path.getParent().toString());
        if (parent != null) {
            parent.addChild(directory);
            directory.setParent(parent);
        }
        return directory;
    }

    private long getImagesCount(Path path) {
        long c = 0;
        try {
            c = Files.walk(path, 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(p -> ExtensionFileFilter.IMAGE.accept(p.toFile()))
                    .count();
        } catch (IOException e) {
            logger.error("Cannot count files {}", path.toString(), e);
        }
        return c;
    }

    public Set<Directory> getTreeAsList() throws DiskException {
        buildFromRoot();
        return new HashSet<>(directories.values());
    }
}
