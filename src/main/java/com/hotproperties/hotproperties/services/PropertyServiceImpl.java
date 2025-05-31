package com.hotproperties.hotproperties.services;

import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.entities.PropertyImage;
import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.exceptions.NotFoundException;
import com.hotproperties.hotproperties.repositories.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserService userService;


    public PropertyServiceImpl(PropertyRepository propertyRepository, UserService userService) {
        this.propertyRepository = propertyRepository;
        this.userService = userService;
    }


    @Override
    public Property registerNewProperty(Property property) {

        User agent = userService.getCurrentUser();
        property.setAgent(agent);
        return propertyRepository.save(property);
    }

    @Override
    public List<Property> getAllProperties(User agent) {
        return propertyRepository.findAllByAgentOrderByTitleDesc(agent);
    }

    @Override
    public void editProperty(Property property) {

    }

    @Override
    public void deletePropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Property not found"));

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

        propertyRepository.deleteById(id);
    }


    @Override
    public void preparePropertyModel(Model model) {

    }

    @Override
    public void prepareEditPropertyModel(Model model) {


    }

    @Override
    public void storePropertyImages(Long id, List<MultipartFile> images) {
        try {
            // Resolve absolute path relative to the project directory
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "property-images");
            Files.createDirectories(uploadPath);  // Ensure path exists

            // Locate property
            Property property = propertyRepository.findById(id).orElseThrow(() -> new NotFoundException("Property not found"));

            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
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
