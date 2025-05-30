package com.hotproperties.hotproperties.services;

import com.hotproperties.hotproperties.entities.Role;
import com.hotproperties.hotproperties.entities.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

public interface UserService {

    @PreAuthorize("isAuthenticated()")
    void prepareDashboardModel(Model model);

    @PreAuthorize("isAuthenticated()")
    void prepareProfileModel(Model model);

    @PreAuthorize("isAuthenticated()")
    void prepareEditProfileModel(Model model);

    @PreAuthorize("isAuthenticated()")
    void updateUserProfile(User updatedUser);

    @PreAuthorize("hasRole('ADMIN')")
    List<User> getAllUsers();

    User registerNewUser(User user, Role role);

    void updateUser(User savedUser);

    @PreAuthorize("isAuthenticated()")
    User getCurrentUser();

    @PreAuthorize("hasRole('ADMIN')")
    void deleteUserByEmail(String userEmail);
}
