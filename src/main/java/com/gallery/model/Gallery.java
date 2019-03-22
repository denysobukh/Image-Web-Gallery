package com.gallery.model;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class Gallery {

    @Autowired
    private Logger logger;

    private final ImageStorage imageStorage;

    public Gallery(ImageStorage imageStorage) {
        this.imageStorage = imageStorage;
    }


    public Collection<Image> getImages() {
        return new ArrayList<Image>();
    }

    public List<Path> getAll() throws IllegalStateException {
        try {
            return imageStorage.getAll();
        } catch (IOException e) {
            throw new IllegalStateException("Error while getting list of images", e);
        }
    }

    @Override
    public String toString() {
        return "This in an image gallery object";
    }

}
