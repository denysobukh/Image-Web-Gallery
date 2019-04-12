package com.gallery.controller;

import com.gallery.application.GalleryException;
import com.gallery.model.MenuItem;
import com.gallery.model.directory.DirectoryWalker;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private DirectoryWalker directoryWalker;


    @ModelAttribute
    public void setPreferences(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        model.addAttribute("userName", session.getId());
        if (session.getAttribute("currentDir") == null) {
            session.setAttribute("currentDir", directoryWalker.getRoot());
        }

    }

    @GetMapping(value = {"/browse", ""})
    public String dir(Model model, HttpServletRequest request, @RequestParam Optional<String> d) throws GalleryException {
        HttpSession session = request.getSession();
        Path currentDir, rootDir;
        currentDir = rootDir = directoryWalker.getRoot();

        if (session.getAttribute("currentDir") != null) {
            currentDir = (Path) session.getAttribute("currentDir");
        }


        if (d.isPresent() && !d.get().equals("")) {
            Path requestedDir = Paths.get(d.get());
            if (!directoryWalker.isWithinRootDir(requestedDir))
                throw new GalleryException("Wrong path " + requestedDir);
            currentDir = rootDir.resolve(requestedDir).normalize();
        } else if (d.isPresent() && d.get().equals("")) {
            currentDir = rootDir;
        }

        session.setAttribute("currentDir", currentDir);

        logger.debug("currentDir = " + currentDir);
        logger.trace("root = " + directoryWalker.getRoot());
        logger.trace("parent = " + directoryWalker.getParent(currentDir));

        List<MenuItem> paths =
                Stream.concat(
                        Stream.of(directoryWalker.getParent(currentDir))
                                .filter(p -> p != null)
                                .map(p -> new MenuItem("..", p.toString())),

                        directoryWalker.listDirs(currentDir).stream()
                                .sorted()
                                .map(p -> new MenuItem(p.getFileName().toString(), p.toString()))
                ).collect(Collectors.toCollection(ArrayList::new));

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
