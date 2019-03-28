package com.gallery.model.directory;

import java.nio.file.Path;
import java.util.List;

/**
 * DirectoryWalkerI specifies the behavior of the directory tree walker
 * Immutable
 *
 * 2019-03-21 16:17 [Thursday]
 *
 * @author Dennis Obukhov
 */
public interface DirectoryWalkerI extends DirectoryWalkerFunctions.RootDirTester,
        DirectoryWalkerFunctions.WithinRootDirTester {

    /**
     * Returns Path of parent directory or null if the given directory is the Root directory
     *
     * @param the given path
     * @return Path of parent directory
     * @throws DirectoryWalkerException if the given path is invalid or filesystem error occurred
     */
    public Path getParent(Path path) throws DirectoryWalkerException;

    /**
     * Returns the list of directories at the given Path or an empty list
     *
     * @param the give path
     * @return the list of directories in the given Path or an empty list
     * @throws DirectoryWalkerException if the given path is invalid or filesystem error occurred
     */
    public List<Path> listDirs(Path path) throws DirectoryWalkerException;

    /**
     * Returns the list of files in the given directory of an empty list
     *
     * @param the given path
     * @return the list of files in the given Path of empty list
     * @throws DirectoryWalkerException if the given path is invalid or filesystem error occurred
     */
    public List<Path> listFiles(Path path) throws DirectoryWalkerException;

    /**
     * Returns the list of files along with directories in the given path
     *
     * @param the given path
     * @return the list of files along with directories in the given path of empty list if
     * @throws DirectoryWalkerException if the given path is invalid or filesystem error occurred
     */
    public List<Path> listAll(Path path) throws DirectoryWalkerException;

    /**
     * Returns the root directory Path
     *
     * @return the root directory Path
     */
    public Path getRoot();

    /**
     * Returns {@code true} if the given directory is the root directory
     *
     * @return {@code true} if the given directory is the root directory
     */
    public boolean isRoot(Path path);


}
