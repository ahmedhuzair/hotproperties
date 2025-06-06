package com.hotproperties.hotproperties.controllers;


import com.hotproperties.hotproperties.entities.Role;
import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.services.AuthService;
import com.hotproperties.hotproperties.services.FavoriteService;
import com.hotproperties.hotproperties.services.MessageService;
import com.hotproperties.hotproperties.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserAccountController {

    private static final Logger logger = LoggerFactory.getLogger(UserAccountController.class);

    private final AuthService authService;
    private final UserService userService;
    private final MessageService messageService;
    private final FavoriteService favoriteService;

    public UserAccountController(AuthService authService, UserService userService, MessageService messageService, FavoriteService favoriteService) {
        this.authService = authService;
        this.userService = userService;
        this.messageService = messageService;
        this.favoriteService = favoriteService;
    }

    @GetMapping({"/", "/index"})
    public String showIndex() {
        return "index";
    }

    // === LOGIN ===
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("user") User user,
                               HttpServletResponse response,
                               Model model) {
        try {
            Cookie jwtCookie = authService.loginAndCreateJwtCookie(user);
            response.addCookie(jwtCookie);
            logger.info("User '{}' logged in.", user.getEmail());
            return "redirect:/dashboard";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    //=== LOGOUT ===
    @GetMapping("/logout")
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    public String logout(HttpServletResponse response) {
        User user = userService.getCurrentUser();
        return "redirect:/login";
    }

    // === DASHBOARD / PROFILE / EDIT PROFILE ===
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','AGENT')")
    public String showDashboard(Model model) {
        userService.prepareDashboardModel(model);
        model.addAttribute("unrepliedMessagesCount", messageService.findUnrepliedMessageCountByAgentId());
        model.addAttribute("countOfFavorites", favoriteService.getFavoriteProperties().size());
        return "dashboard";
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','AGENT')")
    public String showProfile(Model model) {
        userService.prepareProfileModel(model);
        return "profile";
    }

    @GetMapping("/profile/edit")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','AGENT')")
    public String showEditProfile(Model model) {
        userService.prepareEditProfileModel(model);
        return "profile-edit";
    }


    @PostMapping("/profile/edit")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','AGENT')")
    public String editProfile(@ModelAttribute("user") User updatedUser,
                              RedirectAttributes redirectAttributes) {
        try {
            // Look up the real user so we get the correct ID
            User actualUser = userService.getCurrentUser();

            // Copy updates from form-bound user
            actualUser.setFirstName(updatedUser.getFirstName());
            actualUser.setLastName(updatedUser.getLastName());

            userService.updateUserProfile(actualUser);
            logger.info("User: '{} {}' profile updated successfully .", updatedUser.getFirstName(), updatedUser.getLastName());
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + ex.getMessage());
        }
        return "redirect:/profile";
    }


    // === BUYER REGISTRATION ===
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerBuyer(@Valid @ModelAttribute("user") User user,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register"; // show the form again with validation errors
        }
        try {
            // First, register the user (this will assign them an ID)
            User savedUser = userService.registerNewUser(user, Role.ROLE_BUYER);


            redirectAttributes.addFlashAttribute("successMessage", "Buyer registration successful.");
            logger.info("User '{}' registered successfully.", user.getEmail());
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Buyer registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }


}
