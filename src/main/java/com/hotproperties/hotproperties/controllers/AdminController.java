package com.hotproperties.hotproperties.controllers;

import com.hotproperties.hotproperties.entities.Role;
import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }


    // === AGENT REGISTRATION BY ADMIN ONLY ===

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/admin/create-agent")
    public String showCreateAgentForm(Model model) {
        model.addAttribute("user", new User());
        return "create-agent";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/admin/create-agent")
    public String registerAgent(@ModelAttribute("user") User user,
                                RedirectAttributes redirectAttributes) {
        try {
            // First, register the user (this will assign them an ID)
            User savedUser = userService.registerNewUser(user, Role.ROLE_AGENT);


            redirectAttributes.addFlashAttribute("successMessage", "Agent registration successful.");
            return "redirect:/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Agent registration failed: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    // === VIEW ALL USERS FOR ADMIN ONLY ===
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/admin")
    public String viewAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "view-all-users";
    }

    // === DELETE USER FOR ADMIN ONLY ===
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete/user/{email}")
    public String deleteUser(@PathVariable String email, RedirectAttributes redirectAttributes) {
        userService.deleteUserByEmail(email);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        return "redirect:/users/admin";
    }

}
