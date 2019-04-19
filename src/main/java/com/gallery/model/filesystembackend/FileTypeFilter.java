package com.gallery.model.filesystembackend;

/**
 * FileTypeFilter class
 *
 * @author Dennis Obukhov
 * @date 2019-04-14 09:37 [Sunday]
 */
@FunctionalInterface
public interface FileTypeFilter {
    boolean isA(String ext);
}
