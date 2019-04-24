package com.gallery.model.filesystembackend;

/**
 * FileTypeFilterI class
 *
 * @author Dennis Obukhov
 * @date 2019-04-14 09:37 [Sunday]
 */
@FunctionalInterface
public interface FileTypeFilterI {
    boolean isA(String ext);
}
