package com.gallery.model.directory;

import com.gallery.model.ImageExtension;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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

    @FunctionalInterface
    interface RootDirTester {
        boolean isRootDir(Path p);
    }

    @FunctionalInterface
    interface CurrentDirTester {
        boolean isCurrentDir(Path p);
    }

    @FunctionalInterface
    interface WithinRootDirTester {
        boolean isWithinRootDir(Path p);
    }

    /**
     * Provides method to test paths to be used in lambdas below
     */
    public class PathTester implements RootDirTester, CurrentDirTester, WithinRootDirTester {
        @Override
        public boolean isRootDir(Path p) {
            return p.compareTo(currentDir) != 0;
        }

        @Override
        public boolean isCurrentDir(Path p) {
            return p.compareTo(currentDir) != 0;
        }

        @Override
        public boolean isWithinRootDir(Path p) {
            p = rootDir.resolve(p).normalize();
            return Files.isDirectory(p) && p.startsWith(rootDir);
        }
    }

    private PathTester pathTester = new PathTester();

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

    }

    @PostConstruct
    private void postConstruct() {
        logger.debug("Constructed with root = " + rootDir.toAbsolutePath());
    }

    @Override
    public Path getParent() {
        return currentDir.compareTo(rootDir) == 0 ?
                null :
                rootDir.relativize(currentDir.getParent());
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
                    .filter(p -> p.compareTo(currentDir) != 0)
                    .map(p -> rootDir.relativize(p.normalize()))
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
                        return ImageExtension.test(name.substring(i));
                    })
                    .map(p -> rootDir.relativize(p.normalize()))
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
        path = rootDir.resolve(path).normalize();
        logger.debug("Enter " + path);
        if (!pathTester.isWithinRootDir(path))
            throw new DirectoryWalkerException("The path is not a directory or outside the root", path);
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
        return this.getClass().getName() + "(" + rootDir + ")";
    }
}
