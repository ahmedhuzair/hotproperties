package com.hotproperties.hotproperties.controllers;


import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.services.PropertyService;
import com.hotproperties.hotproperties.services.UserService;
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

    public AgentController(UserService userService, PropertyService propertyService) {
        this.userService = userService;
        this.propertyService = propertyService;
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
        try {
            // First, register the Property (this will assign them an ID)
            Property savedProperty = propertyService.registerNewProperty(property);

            // Then, store the property picture (if uploaded)
            if (files != null && !files.isEmpty()) {
                propertyService.storePropertyImages(savedProperty.getId(), files);

            }
            redirectAttributes.addFlashAttribute("successMessage", "Property added successfully");
            return "redirect:/properties/manage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add property. Please try again." + e.getMessage());
            return "redirect:/properties/manage";
        }
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/properties/manage")
    public String viewAllProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties(userService.getCurrentUser()));
        return "/agent/manage-properties";
    }

    // === DELETE PROPERTY FOR AGENT ONLY ===

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/delete/property/{property_id}")
    public String deleteProperty(@PathVariable Long property_id, RedirectAttributes redirectAttributes) {
        propertyService.deletePropertyById(property_id);
        redirectAttributes.addFlashAttribute("successMessage", "Property deleted successfully.");
        return "redirect:/properties/manage";
    }

    // === EDIT PROPERTY FOR AGENT ONLY ===
    @GetMapping("/properties/edit")
    @PreAuthorize("hasRole('AGENT')")
    public String showEditProperty(Model model) {
        propertyService.prepareEditPropertyModel(model);
        return "agent/edit-property";
    }


    @PostMapping("/properties/edit/{property_id}")
    @PreAuthorize("hasRole('AGENT')")
    public String editProperty(@ModelAttribute("user") User updatedUser,
                               RedirectAttributes redirectAttributes) {
        try {
            // Look up the real user so we get the correct ID
            User actualUser = userService.getCurrentUser();

            // Copy updates from form-bound user
            actualUser.setFirstName(updatedUser.getFirstName());
            actualUser.setLastName(updatedUser.getLastName());
            actualUser.setEmail(updatedUser.getEmail());

            userService.updateUserProfile(actualUser);


            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + ex.getMessage());
        }
        return "redirect:/profile";
    }

    //
//    // === PROFILE PICTURE UPLOAD ===
//    @PostMapping("/users/{id}/upload-profile-picture")
//    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
//    public String uploadProfilePicture(@PathVariable Long id,
//                                       @RequestParam("file") MultipartFile file,
//                                       RedirectAttributes redirectAttributes) {
//        try {
//            String filename = userService.storeProfilePicture(id, file);
//            redirectAttributes.addFlashAttribute("message", "Profile picture uploaded: " + filename);
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Upload failed: " + e.getMessage());
//        }
//        return "redirect:/profile";
//    }

//    @GetMapping("/profile-pictures/{filename:.+}")
//    @ResponseBody
//    public ResponseEntity<Resource> serveProfilePicture(@PathVariable String filename) {
//        try {
//            Path filePath = Paths.get("uploads/profile-pictures/").resolve(filename).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//
//            if (resource.exists() && resource.isReadable()) {
//                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
//                        .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
//                        .body(resource);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }


}


