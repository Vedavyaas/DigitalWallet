package com.vedavyaas.apigateway.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

    @GetMapping("/")
    public String root() {
        return "redirect:/ui/login";
    }

    @GetMapping("/ui/login")
    public String login() {
        return "ui/login";
    }

    @GetMapping("/ui/register")
    public String register() {
        return "ui/register";
    }

    @GetMapping("/ui/dashboard")
    public String dashboard() {
        return "ui/dashboard";
    }
}
