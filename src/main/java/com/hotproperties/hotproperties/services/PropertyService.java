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
    List<Property> getAllPropertiesByAgent(User agent);


    @PreAuthorize("hasRole('AGENT')")
    Property getPropertyByIdForCurrentAgent(Long id);

    @PreAuthorize("hasRole('AGENT')")
    void editProperty(Property property);

    @PreAuthorize("hasRole('AGENT')")
    void deletePropertyByIdForCurrentAgent(Long id);

    @PreAuthorize("hasRole('BUYER')")
    List<Property> getFilteredProperties(String zip, Integer minSqFt, Integer minPrice, Integer maxPrice, String sort);

    @PreAuthorize("isAuthenticated()")
    void prepareEditPropertyModel(Model model, Long id);

    @PreAuthorize("hasRole('AGENT')")
    void storePropertyImages(Long id, List<MultipartFile> images);

    @PreAuthorize("hasRole('AGENT')")
    void deletePropertyImage(Long propertyId, Long imageId);

    @PreAuthorize("hasAnyRole('BUYER','AGENT')")
    Property viewPropertyDetail(Long propertyId);

}
