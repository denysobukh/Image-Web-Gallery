package com.gallery.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ErrorController class
 *
 * @author Dennis Obukhov
 * @date 2019-05-23 11:45 [Thursday]
 */
//@ControllerAdvice
public class ErrorController {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public void defaultGlobalHandler() {
        // Nothing to do
    }
}

