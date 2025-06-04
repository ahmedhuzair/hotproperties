package com.hotproperties.hotproperties.controllers;

import com.hotproperties.hotproperties.services.PropertyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FavoriteController {

    private final PropertyService propertyService;

    public FavoriteController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }


    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/favorites/add/{property_id}")
    public String addFavorite(@PathVariable Long property_id) {
        propertyService.addPropertyToFavorites(property_id);
        return "redirect:/properties/view/{property_id}";
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/favorites/remove/{property_id}")
    public String removeFavoriteFromPropertyDetailsPage(@PathVariable Long property_id) {
        propertyService.removePropertyFromFavorites(property_id);
        return "redirect:/properties/view/{property_id}";
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/favorites/remove/favoritesPage/{property_id}")
    public String removeFavoriteFromFavoritesPage(@PathVariable Long property_id) {
        propertyService.removePropertyFromFavorites(property_id);
        return "redirect:/favorites";
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/favorites")
    public String viewFavoriteProperties(Model model) {
        model.addAttribute("properties", propertyService.getFavoriteProperties());
        return "/buyer/saved-favorites";
    }

}
