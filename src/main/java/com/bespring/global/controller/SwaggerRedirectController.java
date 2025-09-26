package com.bespring.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    // Support extension-less access patterns
    @GetMapping({"/api/swagger-ui"})
    public String redirectApiSwaggerUi() {
        return "redirect:/api/swagger-ui.html";
    }

    @GetMapping({"/swagger-ui"})
    public String redirectSwaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}

