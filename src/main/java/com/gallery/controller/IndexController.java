package com.gallery.controller;

import com.gallery.application.GalleryException;
import com.gallery.model.MenuItem;
import com.gallery.model.filesystembackend.DirectoryWalker;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.InvalidPathException;
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
    }

    @GetMapping(value = {"/browse", ""})
    public String browse(Model model, HttpServletRequest request,
                         HttpServletResponse response,
                         @RequestParam Optional<String> d,
                         @CookieValue(value = "currentDir", defaultValue = "") String clientPath) throws GalleryException {

        Path currentDir, rootDir;
        rootDir = directoryWalker.getRoot();

        if (d.isPresent() && !d.get().equals("")) {
            Path requestedDir = Paths.get(d.get());
            if (!directoryWalker.withinRoot(requestedDir)) {
                throw new GalleryException("Wrong path " + requestedDir);
            }
            currentDir = rootDir.resolve(requestedDir).normalize();
        } else if (d.isPresent() && d.get().equals("") || clientPath.equals("")) {
            currentDir = rootDir;
        } else {
            try {
                currentDir = rootDir.resolve(Paths.get(URLDecoder.decode(clientPath, "UTF-8")));
            } catch (InvalidPathException | NullPointerException | UnsupportedEncodingException e) {
                currentDir = rootDir;
            }
        }

        logger.debug("currentDir = " + currentDir);
        logger.trace("root = " + rootDir);

        try {
            Cookie cookie = new Cookie("currentDir", URLEncoder.encode(rootDir.relativize(currentDir).toString(),
                    "UTF-8"));
            cookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.warn("set cookie", e);
        }

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
