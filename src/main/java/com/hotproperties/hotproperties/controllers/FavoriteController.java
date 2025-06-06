package com.hotproperties.hotproperties.controllers;

import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.services.FavoriteService;
import com.hotproperties.hotproperties.services.PropertyService;
import com.hotproperties.hotproperties.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final PropertyService propertyService;

    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);
    private final UserService userService;

    public FavoriteController(PropertyService propertyService, FavoriteService favoriteService, UserService userService) {

        this.favoriteService = favoriteService;
        this.propertyService = propertyService;
        this.userService = userService;
    }



    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/favorites/add/{property_id}")
    public String addFavorite(@PathVariable Long property_id) {
        User user = userService.getCurrentUser();
        favoriteService.addPropertyToFavorites(property_id);
        logger.info("Property '{}' added to favorites list of user '{}'.", propertyService.viewPropertyDetail(property_id).getTitle(), user.getEmail());
        return "redirect:/properties/view/{property_id}";
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/favorites/remove/{property_id}")
    public String removeFavoriteFromPropertyDetailsPage(@PathVariable Long property_id) {
        User user = userService.getCurrentUser();

        favoriteService.removePropertyFromFavorites(property_id);
        logger.info("Property '{}' removed from favorites list of user '{}'.", propertyService.viewPropertyDetail(property_id).getTitle(), user.getEmail());

        return "redirect:/properties/view/{property_id}";
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/favorites/remove/favoritesPage/{property_id}")
    public String removeFavoriteFromFavoritesPage(@PathVariable Long property_id) {
        User user = userService.getCurrentUser();
        favoriteService.removePropertyFromFavorites(property_id);
        logger.info("Property '{}' removed from favorites list of user '{}'.", propertyService.viewPropertyDetail(property_id).getTitle(), user.getEmail());

        return "redirect:/favorites";
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/favorites")
    public String viewFavoriteProperties(Model model) {
        model.addAttribute("properties", favoriteService.getFavoriteProperties());
        return "/buyer/saved-favorites";
    }

}
