package com.gallery.model.file;

import java.util.Arrays;

/**
 * FileExtensionsImage class
 * represents possible extensions of image files
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 09:20 [Friday]
 */
public enum FileExtensionsImage {
    JPEG, JPG, TIFF, TIF, GIF, BMP, PNG;

    /**
     * Tests if the given extension is image extension
     *
     * @param ext to test
     * @return {@code true} if the given extension is image extension
     */
    public static boolean test(String ext) {
        return Arrays.stream(FileExtensionsImage.values()).anyMatch(
                t -> t.toString().equals(ext.toUpperCase())
        );
    }
}
