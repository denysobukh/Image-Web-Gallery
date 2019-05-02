package com.gallery.model.image;

import java.util.Arrays;

/**
 * FileFilter class
 * represents possible directory's types and maps to extension
 * also provides functional interface for test if the given extension String is of a directory type
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 09:20 [Friday]
 */
public enum FileFilter implements FileFilterI {

    /**
     * represents supported images extensions:
     * JPEG, JPG, TIFF, TIF, GIF, BMP, PNG
     */
    IMAGE("jpeg", "jpg", "bmp", "png", "gif"),

    /**
     * represents all extensions that are not empty
     */
    ALL() {
        @Override
        public boolean isA(String ext) {
            return ext.length() > 0;
        }
    };

    private final String[] extensions;

    FileFilter(String... values) {
        this.extensions = values;
    }

    /**
     * Tests if the given name ends with certain string
     *
     * @param name of file
     * @return {@code true} if the given extension is image extension
     */
    public boolean isA(String name) {
        String ext = name.substring(name.toLowerCase().lastIndexOf(".") + 1);
        return Arrays.asList(extensions).contains(ext);
    }
}

