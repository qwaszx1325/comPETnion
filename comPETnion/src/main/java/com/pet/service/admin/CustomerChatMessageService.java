package com.pet.service.admin;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dto.admin.CustomerChatMessageByUserName;
import com.pet.model.admin.CustomerChatMessage;
import com.pet.repository.admin.CustomerChatMessageRepository;


@Service
public class CustomerChatMessageService {
	  private CustomerChatMessageRepository customerChatMessageRepository;

	    @Autowired
	    public CustomerChatMessageService(CustomerChatMessageRepository customerChatMessageRepository) {
	        this.customerChatMessageRepository = customerChatMessageRepository;
	    }
	    
	    public List<CustomerChatMessage> getChatHistory(String senderId, String recipientId) {
	        return customerChatMessageRepository.findChatHistory(senderId, recipientId);
	    }

	    public CustomerChatMessage saveChatMessage(CustomerChatMessage chatMessage) {
	        chatMessage.setTimestamp(new Date());
	        chatMessage.setRead(false);
	        return customerChatMessageRepository.save(chatMessage);
	    }

	    public List<CustomerChatMessage> getUnreadMessages(String recipientId) {
	        return customerChatMessageRepository.findByRecipientIdAndMessageReadFalse(recipientId);
	    }
	    
	    public List<CustomerChatMessageByUserName> getUsersWithAdminChats(String adminId) {
	        return customerChatMessageRepository.findUsersWithAdminChats(adminId);
	    }

	    public void markMessagesAsRead(List<CustomerChatMessage> messages) {
	        messages.forEach(message -> {
	            message.setRead(true);
	            customerChatMessageRepository.save(message);
	        });
	    }
}
