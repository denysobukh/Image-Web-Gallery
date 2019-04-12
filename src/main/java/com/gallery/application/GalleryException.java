package com.gallery.application;

/**
 * GalleryException
 * <p>
 * 2019-03-21 20:18 [Thursday]
 *
 * @author Dennis Obukhov
 */
public class GalleryException extends Exception {

    public GalleryException(String m) {
        super(m);
    }

    public GalleryException(Exception e) {
        super(e);
    }

    public GalleryException(String m, Exception e) {
        super(e);
    }
}
