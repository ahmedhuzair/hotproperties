package com.hotproperties.hotproperties.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AppVersionAdvice {

    @Value("${hotproperties.app.version}")
    private String appVersion;

    @ModelAttribute("appVersion")
    public String addAppVersionToModel() {
        return appVersion;
    }
}
