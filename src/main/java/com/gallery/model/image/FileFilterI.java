package com.gallery.model.image;

/**
 * FileFilterI class
 *
 * @author Dennis Obukhov
 * @date 2019-04-14 09:37 [Sunday]
 */
@FunctionalInterface
public interface FileFilterI {
    boolean isA(String ext);
}
