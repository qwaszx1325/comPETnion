package com.pet.service.memberChat;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dto.memberChat.MemberChatDto;
import com.pet.model.member.Members;
import com.pet.model.memberChat.MemberChat;
import com.pet.repository.member.MembersRepository;
import com.pet.repository.memberChat.MemberChatRepository;

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

	public List<MemberChat> getConversation(Integer senderId, Integer receiverId) {
		Members senderMember = membersRepo.findById(senderId)
				.orElseThrow(() -> new RuntimeException("SenderMember not found"));
		Members receiverMember = membersRepo.findById(receiverId)
				.orElseThrow(() -> new RuntimeException("ReceiverMember not found"));

		List<MemberChat> memberChat = memberChatRepo.findMessages(senderMember, receiverMember);
		memberChat.addAll(memberChatRepo.findMessages(receiverMember, senderMember));
		memberChat.sort(Comparator.comparing(MemberChat::getTimestamp));
		return memberChat;
	}

}
