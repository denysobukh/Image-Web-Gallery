package com.gallery.controller;

import com.gallery.application.GalleryException;
import com.gallery.model.file.ThumbnailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * RestController class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 18:13 [Friday]
 */


@RestController
@RequestMapping(value = "/api")
public class ApiController {

    @Autowired
    private ThumbnailsManager thumbnailsManager;

    @RequestMapping(value = "/scan", produces = "application/json")
    public Map<String, String> scan(Model model) throws GalleryException {
        thumbnailsManager.updateRepository();
        return Collections.singletonMap("message", "Repository update stared");

    }

    @RequestMapping(value = "/cleanup")
    public Map<String, String> cleanup(Model model) {
        thumbnailsManager.cleanUpRepository();
        return Collections.singletonMap("message", "DB was cleaned up");
    }

}
