package com.hotproperties.hotproperties.repositories;

import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface PropertyRepository extends JpaRepository<Property, Long> {


    //Filtering Start

    List<Property> findByLocationEndingWithAndSizeGreaterThanEqualAndPriceBetweenOrderByPriceAsc(
            String zip, Integer minSqFt, Double minPrice, Double maxPrice);

    List<Property> findByLocationEndingWithAndSizeGreaterThanEqualAndPriceBetweenOrderByPriceDesc(
            String zip, Integer minSqFt, Double minPrice, Double maxPrice);

    // ASCENDING ORDER
    List<Property> findByLocationContainingAndSizeGreaterThanEqualAndPriceBetweenOrderByPriceAsc(
            String location, Integer size, Double minPrice, Double maxPrice);

    // DESCENDING ORDER
    List<Property> findByLocationContainingAndSizeGreaterThanEqualAndPriceBetweenOrderByPriceDesc(
            String location, Integer size, Double minPrice, Double maxPrice);

    // get all properties sorted
    List<Property> findAllByOrderByPriceAsc();
    List<Property> findAllByOrderByPriceDesc();

    //Filtering End


    List<Property> findAllByAgentOrderByTitleDesc(User agent);

    List<Property> findAllByOrderByTitleDesc();

    Property findByIdAndAgent(Long id, User agent);


    Property findPropertyById(Long propertyId);

    boolean existsById(Long id);

    boolean existsByTitle(String title);



}
