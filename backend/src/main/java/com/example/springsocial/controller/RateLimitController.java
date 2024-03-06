package com.example.springsocial.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimitController {
    @GetMapping("/user/{id}")
    public String getInfo(@PathVariable("id") String id) {
        return "Hello " + id;
    }
}