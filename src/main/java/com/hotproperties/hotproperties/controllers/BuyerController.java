package com.hotproperties.hotproperties.controllers;

import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.services.FavoriteService;
import com.hotproperties.hotproperties.services.MessageService;
import com.hotproperties.hotproperties.services.PropertyService;
import com.hotproperties.hotproperties.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class BuyerController {

    private final PropertyService propertyService;
    private final FavoriteService favoriteService;


    public BuyerController(PropertyService propertyService, UserService userService, MessageService messageService, FavoriteService favoriteService) {
        this.propertyService = propertyService;
        this.favoriteService = favoriteService;
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/properties/list")
    public String viewAllProperties(@RequestParam(required = false) String zip,
                                    @RequestParam(required = false) Integer minSqFt,
                                    @RequestParam(required = false) Integer minPrice,
                                    @RequestParam(required = false) Integer maxPrice,
                                    @RequestParam(required = false) String sort,
                                    Model model) {
        List<Property> properties = propertyService.getFilteredProperties(zip, minSqFt, minPrice, maxPrice, sort);
        model.addAttribute("propertyCount", properties.size());
        model.addAttribute("properties", properties);
        model.addAttribute("zip", zip);
        model.addAttribute("minSqFt", minSqFt);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortBy", sort);
        return "/buyer/browse-properties";
    }


    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/properties/view/{property_id}")
    public String viewPropertyDetail(Model model, @PathVariable Long property_id) {
        model.addAttribute("property", propertyService.viewPropertyDetail(property_id));
        boolean isFavorite = favoriteService.isPropertyFavoritedByCurrentUser(property_id);
        model.addAttribute("isFavorite", isFavorite);
        return "/buyer/view-property-details";
    }



}
