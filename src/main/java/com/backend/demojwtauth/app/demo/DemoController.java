package com.backend.demojwtauth.app.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("demo")
    public String welcome() {
        return "Welcome from secure endpoint";
    }
}
