package com.gallery.model.filesystembackend;

import java.util.Arrays;

/**
 * FileType class
 * represents possible file's types and maps to extension
 * also provides functional interface for test if the given extension String is of a file type
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 09:20 [Friday]
 */
public enum FileType implements FileTypeFilter {

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

    FileType(String... values) {
        this.extensions = values;
    }

    /**
     * Tests if the given extension
     *
     * @param ext to isTheRootDir
     * @return {@code true} if the given extension is image extension
     */
    public boolean isA(String ext) {
        return Arrays.stream(extensions).anyMatch(
                t -> t.equals(ext.toLowerCase())
        );
    }
}

