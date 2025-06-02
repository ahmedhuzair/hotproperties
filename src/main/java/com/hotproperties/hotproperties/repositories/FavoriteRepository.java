package com.hotproperties.hotproperties.repositories;

import com.hotproperties.hotproperties.entities.Favorite;
import com.hotproperties.hotproperties.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndPropertyId(Long user_id, Long property_id);

    boolean existsByUserIdAndPropertyId(Long user_id, Long property_id);



}
