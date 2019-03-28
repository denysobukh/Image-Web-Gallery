package com.gallery.controller;

import com.gallery.GalleryApplicationException;
import com.gallery.model.MenuItem;
import com.gallery.model.UserPreferences;
import com.gallery.model.directory.DirectoryWalkerI;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

@Controller
public class IndexController {

    @Autowired
    private Logger logger;

    @Autowired
    private DirectoryWalkerI directoryWalker;

    @Autowired
    //session scope bean
    private UserPreferences userPreferences;

    @GetMapping(value = {"/browse", ""})
    public String dir(Model model, HttpServletRequest request, @RequestParam Optional<String> d) throws GalleryApplicationException {

        Path currentDir, rootDir;
        currentDir = rootDir = directoryWalker.getRoot();

        if (d.isPresent() && !d.get().equals("")) {
            Path requestedDir = Paths.get(d.get());
            if (!directoryWalker.isWithinRootDir(requestedDir)) throw new GalleryApplicationException("Wrong path");
            currentDir = rootDir.resolve(requestedDir).normalize();
        } else if (userPreferences.getCurrentDir() != null) {
            currentDir = userPreferences.getCurrentDir();
        }

        userPreferences.setCurrentDir(currentDir);

        logger.debug("currentDir = " + currentDir);
        logger.debug("currentDir = " + Integer.toHexString(currentDir.hashCode()));
        logger.trace("root = " + directoryWalker.getRoot());
        logger.trace("parent = " + directoryWalker.getParent(currentDir));

        List<MenuItem> paths =
                Stream.concat(
                        Stream.of(directoryWalker.getParent(currentDir))
                                .filter(p -> p != null)
                                .map(p -> new MenuItem("..", p.getFileName().toString())),
                        directoryWalker.listDirs(currentDir).stream()
                                .sorted()
                                .map(p -> new MenuItem(p.getFileName().toString(), p.toString()))
                ).collect(Collectors.toCollection(ArrayList::new));

        model.addAttribute("userName", request.getSession().

                getId());
        model.addAttribute("paths", paths);
        model.addAttribute("images", directoryWalker.listFiles(currentDir));
        return "index";
    }

    @GetMapping(value = "/error")
    public String error(ServletRequest request, Model model) {
        Object error = request.getAttribute(ERROR_STATUS_CODE);
        model.addAttribute("error", error);
        return "error_generic";
    }

    @RequestMapping(value = "/login")
    public String login(Model model) {
        return "login";
    }


}
