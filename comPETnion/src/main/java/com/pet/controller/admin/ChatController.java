package com.pet.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.admin.CustomerChatMessage;
import com.pet.service.admin.CustomerChatMessageService;

@RestController
public class ChatController {
	private SimpMessageSendingOperations messagingTemplate;
	private CustomerChatMessageService customerChatMessageService;

	@Autowired
	public ChatController(SimpMessageSendingOperations messagingTemplate,
			CustomerChatMessageService customerChatMessageService, SimpUserRegistry simpUserRegistry) {
		this.messagingTemplate = messagingTemplate;
		this.customerChatMessageService = customerChatMessageService;
		
	}

	@MessageMapping("/customer-service/chat")
	public void sendToCustomerService(@Payload CustomerChatMessage chatMessage, StompHeaderAccessor headerAccessor) {
		String recipientId = chatMessage.getRecipientId();
		String senderId = chatMessage.getSenderId();
		String recipientDestination = "/comPETnion/topic/customer-service/messages/" + recipientId;
		String senderDestination = "/comPETnion/topic/customer-service/messages/" + senderId;
		System.out.println("Received message from " + senderId + " to " + recipientId + ": " + chatMessage.getContent());

		// 傳送消息給對方
		messagingTemplate.convertAndSend(recipientDestination, chatMessage);

		// 傳送消息給自己(要在自己的對話框顯示)
		messagingTemplate.convertAndSend(senderDestination, chatMessage);

		// 保存到數據庫
		customerChatMessageService.saveChatMessage(chatMessage);
	}

	@GetMapping("/api/chat-history")
	public List<CustomerChatMessage> getChatHistory(@RequestParam String senderId, @RequestParam String recipientId) {
		return customerChatMessageService.getChatHistory(senderId, recipientId);
	}

	@GetMapping("/api/unread-messages")
	public List<CustomerChatMessage> getUnreadMessages(@RequestParam String recipientId) {
		List<CustomerChatMessage> unreadMessages = customerChatMessageService.getUnreadMessages(recipientId);
		customerChatMessageService.markMessagesAsRead(unreadMessages);
		return unreadMessages;
	}
}
