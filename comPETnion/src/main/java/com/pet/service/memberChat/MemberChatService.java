package com.pet.service.memberChat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dto.memberChat.MemberChatDto;
import com.pet.model.member.Members;
import com.pet.model.memberChat.MemberChat;
import com.pet.repository.member.MembersRepository;
import com.pet.repository.memberChat.MemberChatRepository;

import jakarta.transaction.Transactional;

@Service
public class MemberChatService {

	@Autowired
	private MemberChatRepository memberChatRepo;
	@Autowired
	private MembersRepository membersRepo;

	public MemberChatDto convertToDto(MemberChat memberChat) {
		MemberChatDto dto = new MemberChatDto();
		dto.setSenderId(memberChat.getSenderId().getMemberId());
		dto.setReceiverId(memberChat.getReceiverId().getMemberId());
		dto.setSenderName(memberChat.getSenderId().getMemberName());
		dto.setReceiverName(memberChat.getReceiverId().getMemberName());
		dto.setContent(memberChat.getContent());
		dto.setTimestamp(memberChat.getTimestamp());
		return dto;
	}

	public MemberChat saveMessage(Integer senderId, Integer receiverId, String content) {
		Members sender = membersRepo.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
		Members receiver = membersRepo.findById(receiverId)
				.orElseThrow(() -> new RuntimeException("Receiver not found"));

		MemberChat memberChat = new MemberChat();
		memberChat.setSenderId(sender);
		memberChat.setReceiverId(receiver);
		memberChat.setContent(content);
		memberChat.setTimestamp(new Date());

		return memberChatRepo.save(memberChat);
	}

	@Transactional
    public List<MemberChatDto> getConversation(Integer senderId, Integer receiverId) {
        Members sender = membersRepo.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        Members receiver = membersRepo.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        List<MemberChat> senderToReceiverChats = memberChatRepo.findMessages(sender, receiver);
        List<MemberChat> receiverToSenderChats = memberChatRepo.findMessages(receiver, sender);

        List<MemberChatDto> allChats = new ArrayList<>();
        for (MemberChat chat : senderToReceiverChats) {
            allChats.add(convertToDto(chat));
        }
        for (MemberChat chat : receiverToSenderChats) {
            allChats.add(convertToDto(chat));
        }

        allChats.sort(Comparator.comparing(MemberChatDto::getTimestamp));

        if (allChats.isEmpty()) {
            MemberChatDto newChat = new MemberChatDto();
            newChat.setSenderId(senderId);
            newChat.setReceiverId(receiverId);
            newChat.setTimestamp(new Date());
            allChats.add(newChat);
        }

        return allChats;
    }
	
	@Transactional
    public List<MemberChatDto> getOrCreateConversation(Integer senderId, Integer receiverId) {
        Members sender = membersRepo.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        Members receiver = membersRepo.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        List<MemberChat> conversation = memberChatRepo.findConversation(sender, receiver);

        if (conversation.isEmpty()) {
            MemberChat newChat = new MemberChat();
            newChat.setSenderId(sender);
            newChat.setReceiverId(receiver);
            newChat.setContent("您好");
            newChat.setTimestamp(new Date());
            conversation.add(memberChatRepo.save(newChat));
        }

        return conversation.stream().map(this::convertToDto).collect(Collectors.toList());
    }
	
//	@Transactional
//    public List<MemberChatDto> getOrCreateConversation(Integer senderId, Integer receiverId) {
//        Members sender = membersRepo.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
//        Members receiver = membersRepo.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));
//
//        List<MemberChat> conversation = memberChatRepo.findConversation(sender, receiver);
//
//        if (conversation.isEmpty()) {
//            // 創建一個空的對話，不添加任何初始化消息
//            MemberChat newChat = new MemberChat();
//            newChat.setSenderId(sender);
//            newChat.setReceiverId(receiver);
//            newChat.setContent("您好"); 
//            newChat.setTimestamp(new Date());
//            memberChatRepo.save(newChat);
//            // 不將這個空消息添加到 conversation 列表中
//        }
//
//        return conversation.stream().map(this::convertToDto).collect(Collectors.toList());
//    }

	public List<MemberChatDto> getConversationList(Integer userId) {
		Members user = membersRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		// 獲取所有相關的聊天記錄
		List<MemberChat> allChats = memberChatRepo.findAllChatsForUser(user);

		// 使用 Map 來確保每個對話會員只出現一次
		Map<Integer, MemberChatDto> conversationMap = new HashMap<>();

		for (MemberChat chat : allChats) {
			Members otherUser = chat.getSenderId().getMemberId().equals(userId) ? chat.getReceiverId()
					: chat.getSenderId();

			Integer otherUserId = otherUser.getMemberId();

			// 如果這個對話會員還沒有在 map 中，或者這條消息比已存在的更新，就更新 map
			if (!conversationMap.containsKey(otherUserId)
					|| chat.getTimestamp().after(conversationMap.get(otherUserId).getTimestamp())) {

				MemberChatDto dto = new MemberChatDto();
				dto.setUserId(otherUserId);
				dto.setUserName(otherUser.getMemberName());
				dto.setLastMessage(chat.getContent());
				dto.setTimestamp(chat.getTimestamp());
				conversationMap.put(otherUserId, dto);
			}
		}

		// 將 map 轉換為列表並按時間降序排序
		return conversationMap.values().stream().sorted(Comparator.comparing(MemberChatDto::getTimestamp).reversed())
				.collect(Collectors.toList());
	}
	
	public List<MemberChatDto> getLatestMessages(Integer userId, Integer otherUserId) {
	    List<MemberChat> messages = memberChatRepo.findLatestMessages(userId, otherUserId);
	    return messages.stream().map(this::convertToDto).collect(Collectors.toList());
	}
	

	

}