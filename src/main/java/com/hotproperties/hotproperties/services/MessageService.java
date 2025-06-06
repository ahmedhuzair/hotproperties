package com.hotproperties.hotproperties.services;

import com.hotproperties.hotproperties.entities.Message;
import com.hotproperties.hotproperties.entities.User;

import java.util.List;

public interface MessageService {
    void sendMessageToAgent(Long property_id, String message);

    void deleteMessageFromBuyer(Long messageId);

    List<Message> getAllMessagesOfBuyer();

    List<Message> getAllMessagesOfAgent();

    void sendReplyToBuyer(Long messageId, String message);

    Message getMessageById(Long messageId);

    void deleteMessageFromAgent(Long messageId);

    int findUnrepliedMessageCountByAgentId();

    User findSenderByMessageId(Long messageId);
}
