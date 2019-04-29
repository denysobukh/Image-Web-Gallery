package com.gallery.controller;

import com.gallery.application.GalleryException;
import com.gallery.model.file.FileRepository;
import com.gallery.model.file.ThumbnailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * AdminController
 * <p>
 * 2019-03-22 15:11 [Friday]
 *
 * @author Dennis Obukhov
 */

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    ThumbnailsManager thumbnailsManager;

    @Autowired
    FileRepository fileRepository;


    @ModelAttribute
    public void setPreferences(Model model, HttpServletRequest request) {
        model.addAttribute("userName", request.getSession().getId());
        long size = fileRepository.count();
        model.addAttribute("statusLine", size + " file(s)");
    }

    @RequestMapping(value = "")
    public String admin(Model model) throws GalleryException {
        return "admin";
    }
}
