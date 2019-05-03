package com.gallery.controller;

import com.gallery.application.ApplicationException;
import com.gallery.model.Disk;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RestController class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 18:13 [Friday]
 */


@RestController
@RequestMapping(value = "/api")
public final class ApiController {


    @Autowired
    Disk storage;
    @Autowired
    Logger logger;

    @RequestMapping(value = "/load-images", produces = "application/json")
    public List<Object> listImages(Model model, @RequestParam("dir") Optional<String> dirOpt) throws ApplicationException {
        Path currentDir, rootDir;
        rootDir = storage.getRoot();

        if (dirOpt.isPresent() && !dirOpt.get().equals("")) {
            Path requestedDir = Paths.get(dirOpt.get());
            if (storage.isOutOfRoot(requestedDir)) {
                throw new ApplicationException("Wrong path " + requestedDir);
            }
            currentDir = rootDir.resolve(requestedDir).normalize();
        } else {
            currentDir = rootDir;
        }
        return storage.listFiles(currentDir, 1)
                .stream()
                .map(path -> new Object() {
                    public String source = path.toString();
                })
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/scan", produces = "application/json")
    public Map<String, String> scan(Model model) throws ApplicationException {
//        thumbnailsManager.updateRepository();
        return Collections.singletonMap("message", "Repository update stared");

    }

    @RequestMapping(value = "/cleanup")
    public Map<String, String> cleanup(Model model) {
//        thumbnailsManager.cleanUpRepository();
        return Collections.singletonMap("message", "DB was cleaned up");
    }

}
