package com.gallery.controller;

import com.gallery.model.Disk;
import com.gallery.model.directory.Directory;
import com.gallery.model.directory.DirectoryRepository;
import com.gallery.model.image.Previewer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

@Controller
public final class IndexController {

    @Autowired
    private Logger logger;

    @Autowired
    private Disk disk;

    @Autowired
    private Previewer previewer;

    @Autowired
    private DirectoryRepository directoryRepository;


    @ModelAttribute
    public void setPreferences(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        model.addAttribute("userName", session.getId());
    }

    @GetMapping(value = "/")
    public String error(Model model) {
        Set<Directory> watched = directoryRepository.findByIsWatched(true);
        disk.filterFromChildren(watched);
        model.addAttribute("watchedList", watched);
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

    @GetMapping(value = "/preview")
    public @ResponseBody
    void preview(HttpServletResponse response, @RequestParam(name = "image") Optional<String> imageSourceOpt) {
        try {
            if (!imageSourceOpt.orElse("").equals("")) {
                String source = imageSourceOpt.get();

                String mime = previewer.getMime(source);
                response.setContentType(mime);

                String headerValue = CacheControl.maxAge(10, TimeUnit.MINUTES).getHeaderValue();
                response.setHeader("Cache-Control", headerValue);

                previewer.writePreview(source, response.getOutputStream());
            }
        } catch (IOException e) {
            logger.warn("Requested preview not found:{}", imageSourceOpt.orElse(""));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }
}
