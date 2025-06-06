package com.hotproperties.hotproperties.services;

import com.hotproperties.hotproperties.entities.Favorite;
import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.exceptions.InvalidFavoriteParameterException;
import com.hotproperties.hotproperties.exceptions.NotFoundException;
import com.hotproperties.hotproperties.repositories.FavoriteRepository;
import com.hotproperties.hotproperties.repositories.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserService userService;
    private final PropertyRepository propertyRepository;

    public FavoriteServiceImpl(UserService userService, PropertyService propertyService, FavoriteRepository favoriteRepository, PropertyRepository propertyRepository) {
        this.userService = userService;
        this.favoriteRepository = favoriteRepository;
        this.propertyRepository = propertyRepository;
    }


    @Override
    public List<Property> getFavoriteProperties() {
        User buyer = userService.getCurrentUser();
        List<Favorite> favorites = buyer.getFavorites();
        List<Property> favoritePropertiesList = new ArrayList<>();
        for (Favorite favorite : favorites) {
            favoritePropertiesList.add(favorite.getProperty());
        }
        return favoritePropertiesList;
    }

    @Override
    @Transactional
    public void addPropertyToFavorites(Long propertyId) {
        User buyer = userService.getCurrentUser();
        if (buyer == null) {
            throw new InvalidFavoriteParameterException("User reference is missing or invalid.");
        }

        if (propertyId == null) {
            throw new InvalidFavoriteParameterException("Property ID is required.");
        }
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new InvalidFavoriteParameterException("Property not found."));
        Favorite favorite = new Favorite(property, buyer);
        favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removePropertyFromFavorites(Long propertyId) {

        User buyer = userService.getCurrentUser();
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new NotFoundException("Property not found"));

        if (!favoriteRepository.existsByUserIdAndPropertyId(buyer.getId(), propertyId)) {
            throw new NotFoundException("Favorite with property ID " + propertyId + " for user " + buyer.getId() + " does not exist");
        }
        Favorite favorite = favoriteRepository.findByUserIdAndPropertyId(buyer.getId(), propertyId)
                .orElseThrow(()-> new NotFoundException("Favorite not found"));
        buyer.getFavorites().remove(favorite);
        property.getFavorites().remove(favorite);

    }

    @Override
    public boolean isPropertyFavoritedByCurrentUser(Long propertyId) {

        User buyer = userService.getCurrentUser();
        return favoriteRepository.existsByUserIdAndPropertyId(buyer.getId(), propertyId);
    }
}
