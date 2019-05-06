package com.gallery.model.image;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 * ExtensionFileFilter class
 * represents possible directory's types and maps to extension
 * also provides functional interface for test if the given extension String is of a directory type
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 09:20 [Friday]
 */
public enum ExtensionFileFilter implements FileFilter {

    /**
     * represents supported images extensions:
     * JPEG, JPG, TIFF, TIF, GIF, BMP, PNG
     */
    IMAGE("jpeg", "jpg", "bmp", "png", "gif"),

    /**
     * represents all extensions that are not empty
     */
    ALL() {
        public boolean accept(String ext) {
            return ext.length() > 0;
        }
    };
    private final String[] extensions;

    ExtensionFileFilter(String... values) {
        this.extensions = values;
    }

    /**
     * Tests if the given name ends with certain string
     *
     * @param file to be tested
     * @return {@code true} if the given file is file and ends with any image's extension
     */
    public boolean accept(File file) {
        if (!file.isFile()) {
            return false;
        }
        String name = file.getName().toLowerCase();
        String ext = name.substring(name.lastIndexOf(".") + 1);
        return Arrays.asList(extensions).contains(ext);
    }
}

