package com.poolviz.pool_viz.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MempoolController {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }
}
