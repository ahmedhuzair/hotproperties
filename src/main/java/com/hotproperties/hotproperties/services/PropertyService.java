package com.hotproperties.hotproperties.services;

import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.entities.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PropertyService {

    @PreAuthorize("hasRole('AGENT')")
    Property registerNewProperty(Property property);


    @PreAuthorize("hasRole('AGENT')")
    List<Property> getAllProperties(User agent);

    @PreAuthorize("hasRole('AGENT')")
    void editProperty(Property property);

    @PreAuthorize("hasRole('AGENT')")
    void deletePropertyByIdForCurrentAgent(Long id);

    @PreAuthorize("isAuthenticated()")
    void preparePropertyModel(Model model);

    @PreAuthorize("isAuthenticated()")
    void prepareEditPropertyModel(Model model);

    @PreAuthorize("hasRole('AGENT')")
    void storePropertyImages(Long id, List<MultipartFile> images);


}
