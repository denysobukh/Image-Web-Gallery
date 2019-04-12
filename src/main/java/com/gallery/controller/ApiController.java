package com.gallery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * RestController class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 18:13 [Friday]
 */


@RestController
public class ApiController {

    @GetMapping(value = "/api/scan-progress")
    Map<String, Integer> scanProgress() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("progress", 99);
        return map;
    }

}
