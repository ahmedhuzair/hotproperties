package com.hotproperties.hotproperties.repositories;

import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findAllByAgentOrderByTitleDesc(User agent);

    Property findByIdAndAgent(Long id, User agent);

    boolean existsById(Long id);

    boolean existsByTitle(String title);

}
