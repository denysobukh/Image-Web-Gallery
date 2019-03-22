package com.gallery.model;

import java.util.HashSet;
import java.util.Set;

public enum FileType {
    JPEG, JPG, TIFF, TIF, GIF, BMP, PNG;

    private static final Set<String> types = new HashSet<>();

    static {
        for (FileType fileType : FileType.values()) {
            types.add(fileType.name());
        }
    }

    public static boolean validate(String type) {
        return types.contains(type.toUpperCase());
    }
}
