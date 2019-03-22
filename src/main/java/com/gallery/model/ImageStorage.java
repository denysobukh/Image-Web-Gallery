package com.gallery.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ImageStorage {
    List<Path> getAll() throws IOException;
}
