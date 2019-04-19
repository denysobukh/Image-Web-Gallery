package com.gallery.controller;

import com.gallery.application.GalleryException;
import com.gallery.model.filesystembackend.ImageFile;
import com.gallery.model.filesystembackend.DirectoryScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    DirectoryScanner directoryScanner;


    @ModelAttribute
    public void setPreferences(Model model, HttpServletRequest request) {
        model.addAttribute("userName", request.getSession().getId());
    }

    @RequestMapping(value = "")
    public String admin(Model model) throws GalleryException {
        return "admin";
    }

    @RequestMapping(value = "/scan")
    public String scan(Model model) throws GalleryException {

        model.addAttribute("message", "Scan started");

        List<String> files = directoryScanner.scan()
                .stream()
                .map(ImageFile::getSourcePath)
                .collect(Collectors.toCollection(LinkedList::new));

        model.addAttribute("files", files);
        return "admin";
    }
}
