package com.gallery.model.file;

import com.gallery.application.GalleryException;

import java.nio.file.Path;

/**
 * The Exception is thrown during file system access errors
 * and all DirectoryTreeWalker errors including internal
 */
public class DirectoryManagerException extends GalleryException {

    @Deprecated
    DirectoryManagerException(Path p) {
        super(p.toString());
    }

    DirectoryManagerException(Exception e) {
        super(e);
    }

    DirectoryManagerException(String m, Path p) {
        super(m + " : " + p);
    }

    DirectoryManagerException(String m, Path p, Exception e) {
        super(m + " : " + p, e);
    }

    DirectoryManagerException(Path p, Exception e) {
        super(p.toString(), e);
    }
}
