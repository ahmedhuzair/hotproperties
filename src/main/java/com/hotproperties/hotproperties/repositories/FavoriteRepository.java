package com.hotproperties.hotproperties.repositories;

import com.hotproperties.hotproperties.entities.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
