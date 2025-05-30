package com.hotproperties.hotproperties.controllers;


import com.hotproperties.hotproperties.entities.Role;
import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.services.AuthService;
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
public class UserAccountController {

    private final AuthService authService;
    private final UserService userService;

    public UserAccountController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
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
            return "redirect:/profile";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    public String logout(HttpServletResponse response) {
        authService.clearJwtCookie(response);
        return "redirect:/auth/login";
    }

    // === DASHBOARD / PROFILE / EDIT PROFILE ===
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','BUYER','AGENT')")
    public String showDashboard(Model model) {
        userService.prepareDashboardModel(model);
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
            actualUser.setEmail(updatedUser.getEmail());

            userService.updateUserProfile(actualUser);


            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + ex.getMessage());
        }
        return "redirect:/profile";
    }


    // === ADMIN + MANAGER VIEWS ===
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/admin/users")
//    public String viewAllUsers(Model model) {
//        model.addAttribute("users", userService.getAllUsers());
//        return "all_users";
//    }



    // === BUYER REGISTRATION ===
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerBuyer(@ModelAttribute("user") User user,
                               RedirectAttributes redirectAttributes) {
        try {
            // First, register the user (this will assign them an ID)
            User savedUser = userService.registerNewUser(user, Role.ROLE_BUYER);


            redirectAttributes.addFlashAttribute("successMessage", "Registration successful.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
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


            redirectAttributes.addFlashAttribute("successMessage", "Agent Registration Successful.");
            return "redirect:/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Agent Registration Failed: " + e.getMessage());
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

    @GetMapping("/profile-pictures/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveProfilePicture(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/profile-pictures/").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
