package com.gallery.model.directory;

import java.nio.file.Path;
import java.util.List;

/**
 * DirectoryWalkerI specifies the behavior of the directory tree walker
 *
 * 2019-03-21 16:17 [Thursday]
 *
 * @author Dennis Obukhov
 */
public interface DirectoryWalkerI {

    /**
     * Returns the directory has been entered by the walker last time
     * or the root folder if no enters has been made
     *
     * @return  java.nio.file.Path
     */
    public Path getCurrent();

    /**
     * Returns Path of parent directory of the current directory
     * or null if current directory is root directory
     * @return Path of parent directory of the current directory
     */
    public Path getParent();

    /**
     * Returns the list of directories in the current folder or empty list
     *
     * @return the list of directories in the current folder or empty list
     * @throws DirectoryWalkerException in case of filesystem error
     */
    public List<Path> listDirs() throws DirectoryWalkerException;

    /**
     *  Returns the list of files in the current folder of empty list
     *
     * @return the list of files in the current folder of empty list
     * @throws DirectoryWalkerException in case of filesystem error
     */
    public List<Path> listFiles() throws DirectoryWalkerException;

    /**
     *  Returns the list of files along with directories in the current folder of empty list
     *
     * @return the list of files along with directories in the current folder of empty list
     * @throws DirectoryWalkerException in case of filesystem error
     */
    public List<Path> listAll() throws DirectoryWalkerException;

    /**
     * Enters the specified directory which becomes current
     *
     * @param path is the directory to enter
     * @throws DirectoryWalkerException if cannot enter the path provided
     */
    public void enter(Path path) throws DirectoryWalkerException;

    /**
     * Enters the parent directory of the current directory
     *
     * @throws DirectoryWalkerException if cannot enter parent directory or when in the root directory
     */
    public void back() throws DirectoryWalkerException;

    /**
     * Returns the root directory Path
     *
     * @return the root directory Path
     */
    public Path getRoot();

    /**
     * Returns {@code true} if current directory is the root directory
     *
     * @return {@code true} if current directory is the root directory
     */
    public boolean isRoot();


}
