package com.vit.ordertracker.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Makes the environment name (dev / test / prod) available to every page. */
@ControllerAdvice
public class GlobalModel {

    @Value("${app.environment}")
    private String environment;

    @ModelAttribute("env")
    public String env() {
        return environment;
    }
}
