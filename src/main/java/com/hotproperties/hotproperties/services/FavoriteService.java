package com.hotproperties.hotproperties.services;

import com.hotproperties.hotproperties.entities.Property;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface FavoriteService {


    List<Property> getFavoriteProperties();

    @PreAuthorize("hasRole('BUYER')")
    void addPropertyToFavorites(Long propertyId);

    @PreAuthorize("hasRole('BUYER')")
    void removePropertyFromFavorites(Long propertyId);

    @PreAuthorize("hasRole('BUYER')")
    boolean isPropertyFavoritedByCurrentUser(Long propertyId);
}
