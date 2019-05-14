package com.gallery.service;

import com.gallery.application.ApplicationException;

import java.nio.file.Path;

/**
 * The Exception is thrown during directory system access errors
 * and all DirectoryTreeWalker errors including internal
 */
public class DiskException extends ApplicationException {

    @Deprecated
    public DiskException(Path p) {
        super(p.toString());
    }

    public DiskException(Exception e) {
        super(e);
    }

    public DiskException(String m, Path p) {
        super(m + " : " + p);
    }

    public DiskException(String m, Path p, Exception e) {
        super(m + " : " + p, e);
    }

    public DiskException(Path p, Exception e) {
        super(p.toString(), e);
    }
}
