package com.hotproperties.hotproperties.repositories;


import com.hotproperties.hotproperties.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String username);

    List<User> findAllByOrderByLastNameAsc();

    void deleteByEmail(String email);
}
