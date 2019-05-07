package com.gallery.controller;

import com.gallery.application.ApplicationException;
import com.gallery.model.Disk;
import com.gallery.model.directory.Directory;
import com.gallery.model.directory.DirectoryRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    private Disk storage;
    @Autowired
    private Logger logger;

    @Autowired
    private DirectoryRepository directoryRepository;

    @RequestMapping(value = "/list-previews", produces = "application/json")
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
                .sorted((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.source, b.source))
                .collect(Collectors.toList());
    }


    @RequestMapping(value = "/watch-directory", produces = "application/json")
    public Map<String, Boolean> watchDirectory(Model model, @RequestParam("dir") Optional<String> pathOpt) throws ApplicationException {
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", false);
        if (pathOpt.isPresent()) {
            String path = pathOpt.get();
            logger.trace("adding watch for " + path);
            Set<Directory> directories = directoryRepository.findByPath(path);
            if (directories.size() == 1) {
                Directory directory = directories.iterator().next();
                boolean isWatched = !directory.isWatched();
                directory.setWatched(isWatched);
                directoryRepository.save(directory);
                response.put("success", true);
                response.put("isWatched", isWatched);
            } else {
                logger.warn("unable to find a directory {}, candidates {}", path, directories.size());
            }
        }
        return response;
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
