package com.hotproperties.hotproperties.services;

import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.repositories.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }


    @Override
    public Property registerNewProperty(Property property) {
        propertyRepository.save(property);
        return property;
    }

    @Override
    public List<Property> getAllProperties() {
        return propertyRepository.findAllByOrderByTitle();
    }

    @Override
    public void editProperty(Property property) {

    }

    @Override
    public void deletePropertyByTitle(Property property) {

    }

    @Override
    public void preparePropertyModel(Model model) {

    }

    @Override
    public void prepareEditPropertyModel(Model model) {

    }
}
