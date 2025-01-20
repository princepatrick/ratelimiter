package com.example.ratelimiter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/")
public class MainController {

    @GetMapping("limited")
    public String getLimitedCalls(){
        return "Limited API: Only limited calls are allowed!!";
    }

    @GetMapping("unlimited")
    public String getUnlimitedCalls(){
        return "Unlimited API: Unlimited calls are allowed!!";
    }

}