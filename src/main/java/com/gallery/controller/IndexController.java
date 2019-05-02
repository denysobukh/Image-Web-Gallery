package com.gallery.controller;

import com.gallery.model.Disk;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

@Controller
public final class IndexController {

    @Autowired
    private Logger logger;

    @Autowired
    private Disk disk;


    @ModelAttribute
    public void setPreferences(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        model.addAttribute("userName", session.getId());
    }


    @GetMapping(value = "/")
    public String error(Model model) {
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
