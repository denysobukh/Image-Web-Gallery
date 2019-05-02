package com.gallery.application;

/**
 * ApplicationException
 * <p>
 * 2019-03-21 20:18 [Thursday]
 *
 * @author Dennis Obukhov
 */
public class ApplicationException extends Exception {

    public ApplicationException(String m) {
        super(m);
    }

    public ApplicationException(Exception e) {
        super(e);
    }

    public ApplicationException(String m, Exception e) {
        super(e);
    }
}
