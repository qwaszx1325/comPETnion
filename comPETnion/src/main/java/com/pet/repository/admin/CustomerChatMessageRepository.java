package com.pet.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.dto.admin.CustomerChatMessageByUserName;
import com.pet.model.admin.CustomerChatMessage;


public interface CustomerChatMessageRepository extends JpaRepository<CustomerChatMessage, Integer>{
	List<CustomerChatMessage> findByRecipientIdAndMessageReadFalse(String recipientId);
	
	@Query("SELECT m FROM CustomerChatMessage m WHERE (m.senderId = :senderId AND m.recipientId = :recipientId) OR (m.senderId = :recipientId AND m.recipientId = :senderId) ORDER BY m.timestamp")
    List<CustomerChatMessage> findChatHistory(String senderId, String recipientId);
	
	@Query("SELECT DISTINCT new com.pet.dto.admin.CustomerChatMessageByUserName(" +
	        "CASE WHEN m.senderId = :adminId THEN m.recipientId ELSE m.senderId END, " +
	        "CASE WHEN m.senderId = :adminId THEN m.recipientName ELSE m.senderName END) " +
	        "FROM CustomerChatMessage m " +
	        "WHERE m.senderId = :adminId OR m.recipientId = :adminId")
	List<CustomerChatMessageByUserName> findUsersWithAdminChats(@Param("adminId") String adminId);
	
	
}
