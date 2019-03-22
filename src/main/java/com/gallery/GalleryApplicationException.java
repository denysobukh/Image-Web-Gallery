package com.gallery;

/**
 * GalleryApplicationException
 * <p>
 * 2019-03-21 20:18 [Thursday]
 *
 * @author Dennis Obukhov
 */
public class GalleryApplicationException extends Exception {

    public GalleryApplicationException(String m) {
        super(m);
    }

    public GalleryApplicationException(Exception e) {
        super(e);
    }

    public GalleryApplicationException(String m, Exception e) {
        super(e);
    }
}
