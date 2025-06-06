package com.hotproperties.hotproperties.controllers;


import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.services.MessageService;
import com.hotproperties.hotproperties.services.PropertyService;
import com.hotproperties.hotproperties.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AgentController {


    private final UserService userService;
    private final PropertyService propertyService;
    private final MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    public AgentController(UserService userService, PropertyService propertyService, MessageService messageService) {
        this.userService = userService;
        this.propertyService = propertyService;
        this.messageService = messageService;
    }

    // === PROPERTY ADDING BY AGENT ONLY ===

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/properties/add")
    public String showAddPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "/agent/add-property";
    }

    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/properties/add")
    public String addProperty(@ModelAttribute("property") Property property, @RequestParam("files") List<MultipartFile> files, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        try {
            // First, register the Property (this will assign them an ID)
            Property savedProperty = propertyService.registerNewProperty(property);

            // Then, store the property picture (if uploaded)
            if (files != null && !files.isEmpty()) {
                propertyService.storePropertyImages(savedProperty.getId(), files);

            }
            redirectAttributes.addFlashAttribute("successMessage", "Property added successfully");
            logger.info("Property '{}' added to Agent '{}' list.", savedProperty.getTitle(), user.getEmail());
            return "redirect:/properties/manage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add property. Please try again." + e.getMessage());
            return "redirect:/properties/add";
        }
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/properties/manage")
    public String viewAllProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllPropertiesByAgent(userService.getCurrentUser()));
        return "/agent/manage-properties";
    }

    // === DELETE PROPERTY FOR AGENT ONLY ===

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/delete/property/{property_id}")
    public String deleteProperty(@PathVariable Long property_id, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        Property propertyToBeDeleted = propertyService.viewPropertyDetail(property_id);
        propertyService.deletePropertyByIdForCurrentAgent(property_id);
        redirectAttributes.addFlashAttribute("successMessage", "Property deleted successfully.");
        logger.info("Property '{}' removed from Agent '{}' list.", propertyToBeDeleted.getTitle(), user.getEmail());
        return "redirect:/properties/manage";
    }

    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/properties/{propertyId}/images/{imageId}/delete")
    public String deletePropertyImage(@PathVariable Long propertyId,
                                      @PathVariable Long imageId,
                                      RedirectAttributes redirectAttributes) {
        try {
            propertyService.deletePropertyImage(propertyId, imageId);
            redirectAttributes.addFlashAttribute("successMessage", "Image deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete image: " + e.getMessage());
        }
        return "redirect:/properties/edit/" + propertyId;
    }

    // === EDIT PROPERTY FOR AGENT ONLY ===
    @GetMapping("/properties/edit/{property_id}")
    @PreAuthorize("hasRole('AGENT')")
    public String showEditProperty(Model model, @PathVariable Long property_id) {
        propertyService.prepareEditPropertyModel(model, property_id);
        return "/agent/edit-property";
    }


    @PostMapping("/properties/edit/{property_id}")
    @PreAuthorize("hasRole('AGENT')")
    public String editProperty(@ModelAttribute("property") Property updatedProperty,
                               @RequestParam("files") List<MultipartFile> files,
                               RedirectAttributes redirectAttributes,
                               @PathVariable Long property_id) {
        try {

            Property actualProperty = propertyService.getPropertyByIdForCurrentAgent(property_id);
            if (actualProperty == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Property not found.");
                return "redirect:/properties/manage";
            }

            actualProperty.setTitle(updatedProperty.getTitle());
            actualProperty.setDescription(updatedProperty.getDescription());
            actualProperty.setPrice(updatedProperty.getPrice());
            actualProperty.setLocation(updatedProperty.getLocation());
            actualProperty.setSize(updatedProperty.getSize());

            propertyService.editProperty(actualProperty);

            if (files != null && !files.isEmpty()) {
                propertyService.storePropertyImages(actualProperty.getId(), files);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Property updated successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update property: " + ex.getMessage());
        }
        return "redirect:/properties/manage";
    }


}


