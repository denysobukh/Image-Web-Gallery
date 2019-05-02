package com.gallery.model;

import com.gallery.application.ApplicationException;

import java.nio.file.Path;

/**
 * The Exception is thrown during directory system access errors
 * and all DirectoryTreeWalker errors including internal
 */
public class DiskException extends ApplicationException {

    @Deprecated
    DiskException(Path p) {
        super(p.toString());
    }

    DiskException(Exception e) {
        super(e);
    }

    DiskException(String m, Path p) {
        super(m + " : " + p);
    }

    DiskException(String m, Path p, Exception e) {
        super(m + " : " + p, e);
    }

    DiskException(Path p, Exception e) {
        super(p.toString(), e);
    }
}
