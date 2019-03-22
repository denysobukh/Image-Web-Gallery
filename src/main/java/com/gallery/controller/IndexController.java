package com.gallery.controller;

import com.gallery.ApplicationException;
import com.gallery.model.directory.DirectoryWalkerI;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletRequest;

import static javax.servlet.RequestDispatcher.ERROR_STATUS_CODE;

@Controller
public class DefaultController {

    @Autowired
    private Logger logger;

    @Autowired
    private DirectoryWalkerI directoryWalker;

    @GetMapping(value = {"", "/"})
    public String index(Model model) throws ApplicationException {
        model.addAttribute("name", "Username");
        model.addAttribute("gallery", directoryWalker);
        model.addAttribute("paths", directoryWalker.listDirs());
        return "index";
    }

    @GetMapping(value = "/error")
    public String error(ServletRequest request, Model model) {
        Object error = request.getAttribute(ERROR_STATUS_CODE);
        model.addAttribute("error", error);
        return "error";
    }

    @RequestMapping(value = "/login")
    public String login(Model model) {
        return "login";
    }

    @ExceptionHandler(ApplicationException.class)
    public ModelAndView exceptionHandler(ApplicationException e) {
        ModelAndView mv = new ModelAndView("errors/generic");
        mv.addObject("exception", e);
        return mv;
    }


}
