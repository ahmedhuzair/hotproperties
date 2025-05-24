package com.hotproperties.hotproperties.repositories;

import com.hotproperties.hotproperties.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
