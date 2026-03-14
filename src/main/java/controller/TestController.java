package com.Sahil.job_portal_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public  class TestController {

    @GetMapping("/")
    public String home() {
        return "Job Portal API is running 🚀";
    }

}