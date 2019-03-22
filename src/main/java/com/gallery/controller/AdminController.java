package com.gallery.controller;

import com.gallery.GalleryApplicationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * AdminController
 * <p>
 * 2019-03-22 15:11 [Friday]
 *
 * @author Dennis Obukhov
 */

@Controller
public class AdminController {

    @RequestMapping(value = "/admin")
    public String admin(Model model) throws GalleryApplicationException {
        throw new GalleryApplicationException("test");
        //return "index";
    }


}
