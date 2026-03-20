package com.mcart.productservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VersionController {

    @GetMapping("/version")
    public String version() {
        return "Product Service CI/CD Version 2";
    }
}