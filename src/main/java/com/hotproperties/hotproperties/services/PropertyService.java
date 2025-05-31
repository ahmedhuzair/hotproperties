package com.hotproperties.hotproperties.services;

import com.hotproperties.hotproperties.entities.Property;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

import java.util.List;

public interface PropertyService {

    @PreAuthorize("hasRole('AGENT')")
    Property registerNewProperty(Property property);


    @PreAuthorize("hasRole('AGENT')")
    List<Property> getAllProperties();

    @PreAuthorize("hasRole('AGENT')")
    void editProperty(Property property);

    @PreAuthorize("hasRole('AGENT')")
    void deletePropertyById(Long id);

    @PreAuthorize("isAuthenticated()")
    void preparePropertyModel(Model model);

    @PreAuthorize("isAuthenticated()")
    void prepareEditPropertyModel(Model model);


}
