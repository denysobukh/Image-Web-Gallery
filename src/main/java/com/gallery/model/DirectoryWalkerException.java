package com.gallery.model;

import com.gallery.ApplicationException;

import java.nio.file.Path;

/**
 * The Exception is thrown during file system access errors
 * and all DirectoryTreeWalker errors including internal
 */
public class DirectoryWalkerException extends ApplicationException {

    @Deprecated
    DirectoryWalkerException(Path p) {
        super(p.toString());
    }

    DirectoryWalkerException(Exception e) {
        super(e);
    }

    DirectoryWalkerException(String m) {
        super(m);
    }

    DirectoryWalkerException(String m, Path p) {
        super(m + ":" + p);
    }

    DirectoryWalkerException(String m, Path p, Exception e) {
        super(m + ":" + p, e);
    }
    DirectoryWalkerException(Path p, Exception e) {
        super(p.toString(), e);
    }
}
