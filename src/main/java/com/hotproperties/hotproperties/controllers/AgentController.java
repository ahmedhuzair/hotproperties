package com.hotproperties.hotproperties.controllers;


import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.entities.Role;
import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.services.AuthService;
import com.hotproperties.hotproperties.services.PropertyService;
import com.hotproperties.hotproperties.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AgentController {


    private final UserService userService;
    private final PropertyService propertyService;

    public AgentController(UserService userService, PropertyService propertyService) {
        this.userService = userService;
        this.propertyService = propertyService;
    }

    // === PROPERTY REGISTRATION BY AGENT ONLY ===

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/properties/add")
    public String showAddPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "add-property";
    }

    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/properties/add")
    public String addProperty(@ModelAttribute("property") Property property, RedirectAttributes redirectAttributes){
        try {
            // First, register the Property (this will assign them an ID)
            Property savedProperty = propertyService.registerNewProperty(property);
            redirectAttributes.addFlashAttribute("successMessage", "Property Registration Successful.");
            return "redirect:/manage-properties";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Property Registration Failed: " + e.getMessage());
            return "redirect:/manage-properties";
        }
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/properties/manage")
    public String viewAllProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "manage-properties";
    }



}


