package com.hotproperties.hotproperties.repositories;

import com.hotproperties.hotproperties.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
