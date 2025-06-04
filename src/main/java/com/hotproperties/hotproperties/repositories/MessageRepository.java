package com.hotproperties.hotproperties.repositories;

import com.hotproperties.hotproperties.entities.Message;
import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllBySenderIs(User sender);
    List<Message> findAllByPropertyIn(List<Property> properties);
    List<Message> findByPropertyInAndReplyIsNull(List<Property> properties);
}
