package com.hotproperties.hotproperties.services;

import com.hotproperties.hotproperties.entities.Message;
import com.hotproperties.hotproperties.entities.Property;
import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.exceptions.InvalidMessageParameterException;
import com.hotproperties.hotproperties.exceptions.NotFoundException;
import com.hotproperties.hotproperties.repositories.FavoriteRepository;
import com.hotproperties.hotproperties.repositories.MessageRepository;
import com.hotproperties.hotproperties.repositories.PropertyRepository;
import com.hotproperties.hotproperties.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {
    private final UserService userService;
    private final PropertyRepository propertyRepository;
    private final MessageRepository messageRepository;

    public MessageServiceImpl(UserService userService, PropertyRepository propertyRepository, MessageRepository messageRepository) {
        this.userService = userService;
        this.propertyRepository = propertyRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void sendMessageToAgent(Long property_id, String message) {

        User sender = userService.getCurrentUser();
        Property property = propertyRepository.findById(property_id).orElseThrow(() -> new NotFoundException("Property not found"));
        if (message == null || message.isEmpty()) {
            throw new InvalidMessageParameterException("Message content must not be empty.");
        }
        if (sender == null) {
            throw new InvalidMessageParameterException("Sender must not be null.");
        }
        if (property == null) {
            throw new InvalidMessageParameterException("Property not found.");
        }

        Message messageToSend = new Message(message, property, sender);
        messageRepository.save(messageToSend);
    }


    @Override
    public List<Message> getAllMessagesOfBuyer() {
        User buyer = userService.getCurrentUser();
        return messageRepository.findAllBySenderIs(buyer);
    }

    @Override
    public List<Message> getAllMessagesOfAgent() {
        User agent = userService.getCurrentUser();
        List<Property> agentProperties = agent.getProperties();
        return messageRepository.findAllByPropertyIn(agentProperties);
    }

    @Override
    public void sendReplyToBuyer(Long messageId, String message) {
        Message replyMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));

        replyMessage.setReply(message);
        if (message == null || message.isEmpty()) {
            throw new InvalidMessageParameterException("Message content must not be empty.");
        }

        messageRepository.save(replyMessage);
    }

    @Override
    public Message getMessageById(Long messageId) {

        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));
    }
    @Override
    @Transactional
    public void deleteMessageFromBuyer(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));

        User buyer = userService.getCurrentUser();
        buyer.getMessages().remove(message);

    }

    @Override
    @Transactional
    public void deleteMessageFromAgent(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));
        User agent = userService.getCurrentUser();
        Property property = message.getProperty();
        agent.getMessages().remove(message);



    }

}
