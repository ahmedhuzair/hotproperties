package com.hotproperties.hotproperties.services;

import com.hotproperties.hotproperties.entities.*;
import com.hotproperties.hotproperties.exceptions.InvalidFavoriteParameterException;
import com.hotproperties.hotproperties.exceptions.InvalidPropertyImageParameterException;
import com.hotproperties.hotproperties.exceptions.InvalidPropertyParameterException;
import com.hotproperties.hotproperties.exceptions.NotFoundException;
import com.hotproperties.hotproperties.repositories.FavoriteRepository;
import com.hotproperties.hotproperties.repositories.MessageRepository;
import com.hotproperties.hotproperties.repositories.PropertyRepository;
import com.hotproperties.hotproperties.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserService userService;
    private final UserRepository userRepository;


    public PropertyServiceImpl(PropertyRepository propertyRepository, UserService userService, UserRepository userRepository, FavoriteRepository favoriteRepository, FavoriteRepository favoriteRepository1, MessageRepository messageRepository) {
        this.propertyRepository = propertyRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }


    @Override
    public Property registerNewProperty(Property property) {
        if (property.getTitle() == null || property.getTitle().isBlank()
                || property.getPrice() == null
                || property.getLocation() == null || property.getLocation().isBlank()
                || property.getSize() == null) {
            throw new InvalidPropertyParameterException(" Title, price, location, and size are required.");
        }
        if (propertyRepository.existsByTitle(property.getTitle())) {
            throw new InvalidPropertyParameterException(" Property already exists.");
        }
        User agent = userService.getCurrentUser();
        property.setAgent(agent);
        return propertyRepository.save(property);
    }

    @Override
    public List<Property> getAllPropertiesByAgent(User agent) {
        return propertyRepository.findAllByAgentOrderByTitleDesc(agent);
    }

    @Override
    public List<Property> getAllProperties() {
        return propertyRepository.findAllByOrderByTitleDesc();
    }

    @Override
    public Property getPropertyByIdForCurrentAgent(Long id) {
        User agent = userService.getCurrentUser();
        if (!propertyRepository.existsById(id)) {
            throw new NotFoundException("Property not found");
        }
        return propertyRepository.findByIdAndAgent(id, agent);
    }

    @Override
    public void editProperty(Property updatedProperty) {
        User agent = userService.getCurrentUser();

        if (updatedProperty.getTitle() == null || updatedProperty.getTitle().isBlank()
                || updatedProperty.getPrice() == null
                || updatedProperty.getLocation() == null || updatedProperty.getLocation().isBlank()
                || updatedProperty.getSize() == null) {
            throw new InvalidPropertyParameterException(" Title, price, location and size are required.");
        }

        Property existingProperty = propertyRepository.findById(updatedProperty.getId())
                .orElseThrow(() -> new NotFoundException("Property not found"));

        if (!existingProperty.getAgent().getId().equals(agent.getId())) {
            throw new RuntimeException("You do not own this property.");
        }

        existingProperty.setTitle(updatedProperty.getTitle());
        existingProperty.setDescription(updatedProperty.getDescription());
        existingProperty.setPrice(updatedProperty.getPrice());
        existingProperty.setLocation(updatedProperty.getLocation());
        propertyRepository.save(existingProperty);

    }

    @Transactional
    @Override
    public void deletePropertyByIdForCurrentAgent(Long id) {
        User agent = userService.getCurrentUser();
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Property not found"));

        if (!agent.getProperties().contains(property)) {
            throw new RuntimeException("You do not own this property.");
        }

        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "property-images");

        if (property.getImages() != null) {
            for (PropertyImage propertyImage : property.getImages()) {
                Path oldPath = uploadPath.resolve(propertyImage.getImageFileName());
                try {
                    Files.deleteIfExists(oldPath);
                } catch (IOException ex) {
                    System.out.println("Failed to delete property image: " + ex.getMessage());
                    throw new RuntimeException("Failed to delete property images", ex);
                }
            }
        }
        agent.getProperties().remove(property);
        userRepository.save(agent);
    }

    @Override
    public void deletePropertyImage(Long propertyId, Long imageId) {
        User agent = userService.getCurrentUser();
        Property property = propertyRepository.findByIdAndAgent(propertyId, agent);
        if (property == null) {
            throw new NotFoundException("Property not found");
        }

        PropertyImage imageToDelete = property.getImages().stream()
                .filter(image -> image.getId() == imageId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Image not found"));

        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "property-images");
        Path imagePath = uploadPath.resolve(imageToDelete.getImageFileName());

        try {
            Files.deleteIfExists(imagePath);
            property.getImages().remove(imageToDelete);
            propertyRepository.save(property);
        } catch (IOException ex) {
            System.out.println("Failed to delete image: " + ex.getMessage());
            throw new RuntimeException("Failed to delete property image", ex);
        }
    }

    @Override
    public Property viewPropertyDetail(Long propertyId) {
        return propertyRepository.findPropertyById(propertyId);

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
    public List<Property> getFilteredProperties(String zip, Integer minSqFt, Integer minPrice, Integer maxPrice, String sort) {
        // Handle null/defaults
        String locationFilter;
        if (zip == null || zip.isEmpty()) {
            locationFilter = "";
        } else {
            locationFilter = zip;
        }

        Integer minSqFtFilter;
        if (minSqFt == null) {
            minSqFtFilter = 0;
        } else {
            minSqFtFilter = minSqFt;
        }

        Double minPriceFilter;
        if (minPrice == null) {
            minPriceFilter = 0.0;
        } else {
            minPriceFilter = minPrice.doubleValue();
        }

        Double maxPriceFilter;
        if (maxPrice == null) {
            maxPriceFilter = Double.MAX_VALUE;
        } else {
            maxPriceFilter = maxPrice.doubleValue();
        }

        boolean sortDesc = "desc".equalsIgnoreCase(sort);

        // If ZIP is entered, match on location ending with zip
        if (!locationFilter.isEmpty()) {
            if (sortDesc) {
                return propertyRepository.findByLocationEndingWithAndSizeGreaterThanEqualAndPriceBetweenOrderByPriceDesc(
                        locationFilter, minSqFtFilter, minPriceFilter, maxPriceFilter
                );
            } else {
                return propertyRepository.findByLocationEndingWithAndSizeGreaterThanEqualAndPriceBetweenOrderByPriceAsc(
                        locationFilter, minSqFtFilter, minPriceFilter, maxPriceFilter
                );
            }
        }

        // General filter with location containing
        if (sortDesc) {
            return propertyRepository.findByLocationContainingAndSizeGreaterThanEqualAndPriceBetweenOrderByPriceDesc(
                    locationFilter, minSqFtFilter, minPriceFilter, maxPriceFilter
            );
        } else {
            return propertyRepository.findByLocationContainingAndSizeGreaterThanEqualAndPriceBetweenOrderByPriceAsc(
                    locationFilter, minSqFtFilter, minPriceFilter, maxPriceFilter
            );
        }
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
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new NotFoundException("Property not found"));
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

    @Override

    public void prepareEditPropertyModel(Model model, Long id) {
        User agent = userService.getCurrentUser();
        Property properties = propertyRepository.findByIdAndAgent(id, agent);
        if (properties == null) {
            throw new NotFoundException("Property not found");
        }
        model.addAttribute("property", properties);
        model.addAttribute("images", properties.getImages());
        model.addAttribute("agent", agent);
    }

    @Override
    public void storePropertyImages(Long propertyId, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new InvalidPropertyImageParameterException("No image files provided.");
        }
        try {
            // Resolve absolute path relative to the project directory
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "property-images");
            Files.createDirectories(uploadPath);  // Ensure path exists

            Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new InvalidPropertyImageParameterException("Property not found for image."));

            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {

                    String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                if(!(filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg") || filename.toLowerCase().endsWith(".png") || filename.toLowerCase().endsWith(".webp") || filename.toLowerCase().endsWith(".heic"))) {
                        throw new InvalidPropertyImageParameterException("Invalid image format");
                    }
                    Path filePath = uploadPath.resolve(filename);
                    image.transferTo(filePath.toFile());

                    PropertyImage propertyImage = new PropertyImage();
                    propertyImage.setImageFileName(filename);
                    property.addImage(propertyImage);
                }
            }
            propertyRepository.save(property);
        } catch (IOException ex) {
            System.out.println("Failed to save image: " + ex.getMessage());
            throw new RuntimeException("Failed to save property images", ex);
        }

    }

}
