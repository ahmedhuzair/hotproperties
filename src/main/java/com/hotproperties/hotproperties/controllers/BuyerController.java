package com.hotproperties.hotproperties.controllers;

import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.services.PropertyService;
import com.hotproperties.hotproperties.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BuyerController {

    private final PropertyService propertyService;
    private final UserService userService;

    public BuyerController(PropertyService propertyService, UserService userService) {
        this.propertyService = propertyService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/properties/list")
    public String viewAllProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "/buyer/browse-properties";
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/properties/view/{property_id}")
    public String viewPropertyDetail(Model model, @PathVariable Long property_id) {
        model.addAttribute("property", propertyService.viewPropertyDetail(property_id));
        return "/buyer/view-property-details";
    }

}
