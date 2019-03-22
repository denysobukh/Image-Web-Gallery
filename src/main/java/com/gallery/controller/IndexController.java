package com.gallery.controller;

import com.gallery.GalleryApplicationException;
import com.gallery.model.directory.DirectoryWalkerI;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

@Controller
public class IndexController {

    @Autowired
    private Logger logger;

    @Autowired
    private DirectoryWalkerI directoryWalker;

    @GetMapping(value = {"", "/"})
    public String index(Model model) throws GalleryApplicationException {
        model.addAttribute("name");
        model.addAttribute("paths", directoryWalker.listDirs());
        return "index";
    }

    @GetMapping(value = {"/browse"})
    public String dir(Model model, HttpServletRequest request, @RequestParam Optional<String> d) throws GalleryApplicationException {
        String requestedDir = d.orElse("");
        logger.debug("requestedDir=" + requestedDir);

        model.addAttribute("name", request.getSession().getId());
        model.addAttribute("paths",
                directoryWalker.listDirs()
                        .stream()
                        .map(p -> p.getFileName().toString())
                        .collect(Collectors.toCollection(ArrayList::new)));
        model.addAttribute("images", directoryWalker.listFiles());
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

    /*
    @ExceptionHandler(GalleryApplicationException.class)
    public ModelAndView exceptionHandler(GalleryApplicationException e) {
        ModelAndView mv = new ModelAndView("errors/generic");
        mv.addObject("exception", e);
        return mv;
    }
*/

}
