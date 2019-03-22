package com.gallery.model;

import java.util.HashSet;
import java.util.Set;

public enum ImageExtension {
    JPEG, JPG, TIFF, TIF, GIF, BMP, PNG;

    private static final Set<String> extensions = new HashSet<>();

    static {
        for (ImageExtension e : ImageExtension.values()) {
            extensions.add(e.name());
        }
    }

    public static boolean test(String type) {
        return extensions.contains(type.toUpperCase());
    }
}
