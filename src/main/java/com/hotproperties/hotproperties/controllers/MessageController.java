package com.hotproperties.hotproperties.controllers;

import com.hotproperties.hotproperties.services.MessageService;
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

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    //BUYER METHODS
    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/properties/message/{property_id}")
    public String sendMessageToAgent(@PathVariable Long property_id,
                                     @RequestParam(required = false) String message,
                                     RedirectAttributes redirectAttributes) {

        messageService.sendMessageToAgent(property_id, message);
        redirectAttributes.addFlashAttribute("successMessage", "Message sent successfully.");
        return "redirect:/properties/view/" + property_id;
    }


    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/messages/buyer")
    public String viewMessagesBuyer(Model model) {
        model.addAttribute("messages", messageService.getAllMessagesOfBuyer());
        return "/buyer/view-all-messages";
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/delete/message/{messageId}")
    public String deleteMessageFromBuyer(@PathVariable Long messageId) {
        messageService.deleteMessageFromBuyer(messageId);
        return "redirect:/messages/buyer";
    }

    //AGENT METHODS
    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/messages/agent")
    public String viewAllMessagesAgent(Model model) {
        model.addAttribute("messages", messageService.getAllMessagesOfAgent());
        return "/agent/view-all-messages";
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/messages/{message_id}")
    public String viewMessageDetailsAgent(@PathVariable Long message_id, Model model) {
        model.addAttribute("message", messageService.getMessageById(message_id));
        return "/agent/view-message-details";
    }

    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/properties/replymessage/{message_id}")
    public String sendReplyToBuyer(@PathVariable Long message_id,
                                   @RequestParam("reply") String message,
                                   RedirectAttributes redirectAttributes) {

        messageService.sendReplyToBuyer(message_id, message);
        redirectAttributes.addFlashAttribute("successMessage", "Reply sent successfully.");
        return "redirect:/messages/agent";
    }

    @PreAuthorize("hasRole('AGENT')")
    @GetMapping("/delete/message/agent/{messageId}")
    public String deleteMessageFromAgent(@PathVariable Long messageId) {
        messageService.deleteMessageFromAgent(messageId);
        return "redirect:/messages/agent";
    }
}
