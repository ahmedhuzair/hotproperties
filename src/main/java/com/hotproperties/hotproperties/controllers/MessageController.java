package com.hotproperties.hotproperties.controllers;

import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.services.MessageService;
import com.hotproperties.hotproperties.services.PropertyService;
import com.hotproperties.hotproperties.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MessageController {

    private final MessageService messageService;
    private final PropertyService propertyService;

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final UserService userService;

    public MessageController(MessageService messageService, PropertyService propertyService, UserService userService) {
        this.messageService = messageService;
        this.propertyService = propertyService;
        this.userService = userService;
    }

    //BUYER METHODS
    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/properties/message/{property_id}")
    public String sendMessageToAgent(@PathVariable Long property_id,
                                     @RequestParam(required = false) String message,
                                     RedirectAttributes redirectAttributes) {

        messageService.sendMessageToAgent(property_id, message);
        User sender = userService.getCurrentUser();

        redirectAttributes.addFlashAttribute("successMessage", "Message sent successfully.");
        logger.info("Message '{}' sent to Agent '{}' from Buyer '{}' ", message, propertyService.viewPropertyDetail(property_id).getAgent().getEmail(), sender.getEmail() );
        return "redirect:/properties/view/" + property_id;
    }


    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/messages/buyer")
    public String viewMessagesBuyerInbox(Model model) {
        model.addAttribute("messages", messageService.getAllMessagesOfBuyer());
        return "/buyer/view-all-messages";
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/delete/message/{messageId}")
    public String deleteMessageFromBuyerInbox(@PathVariable Long messageId) {
        messageService.deleteMessageFromBuyer(messageId);
        return "redirect:/messages/buyer";
    }

    //AGENT METHODS
    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/messages/agent")
    public String viewMessagesAgentInbox(Model model) {
        model.addAttribute("messages", messageService.getAllMessagesOfAgent());
        return "/agent/view-all-messages";
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/messages/{message_id}")
    public String viewMessageDetailsForAgent(@PathVariable Long message_id, Model model) {
        model.addAttribute("message", messageService.getMessageById(message_id));
        return "/agent/view-message-details";
    }

    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/properties/replymessage/{message_id}")
    public String sendReplyToBuyer(@PathVariable Long message_id,
                                   @RequestParam("reply") String message,
                                   RedirectAttributes redirectAttributes) {

        messageService.sendReplyToBuyer(message_id, message);
        User agent = userService.getCurrentUser();
        User buyer = messageService.findSenderByMessageId(message_id);
        redirectAttributes.addFlashAttribute("successMessage", "Reply sent successfully.");
        logger.info("Reply: '{}' sent to Buyer '{}' from Agent '{}' ", message, buyer.getEmail(), agent.getEmail() );
        return "redirect:/messages/agent";
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/delete/message/agent/{messageId}")
    public String deleteMessageFromAgentInbox(@PathVariable Long messageId) {
        messageService.deleteMessageFromAgent(messageId);
        return "redirect:/messages/agent";
    }
}
