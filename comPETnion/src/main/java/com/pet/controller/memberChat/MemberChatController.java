package com.pet.controller.memberChat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pet.dto.memberChat.MemberChatDto;
import com.pet.model.member.Members;
import com.pet.model.memberChat.MemberChat;
import com.pet.service.member.MembersService;
import com.pet.service.memberChat.MemberChatService;

import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
public class MemberChatController {

	@Autowired
	private MemberChatService memberChatService;
	@Autowired
	private MembersService membersService;

	@GetMapping("/member/chat/{receiverId}")
	public String chatView(@PathVariable Integer receiverId, Model model, HttpSession httpSession) {
		Members member = (Members) httpSession.getAttribute("member");
		if (member == null) {
			return "redirect:/member/members/login";
		}
		Members receiver = membersService.findMembersById(receiverId);

		model.addAttribute("senderId", member.getMemberId());
		model.addAttribute("senderName", member.getMemberName());
		model.addAttribute("receiverId", receiverId);
		model.addAttribute("receiverName", receiver.getMemberName());
		return "memberChat/memberChat";
	}

	@PostMapping("/api/messages")
	@ResponseBody
	public ResponseEntity<MemberChat> sendMessage(@RequestBody MemberChatDto memberChatDto, HttpSession httpSession) {
		MemberChat memberChat = memberChatService.saveMessage(memberChatDto.getSenderId(),
				memberChatDto.getReceiverId(), memberChatDto.getContent());
		return ResponseEntity.ok(memberChat);
	}

	@GetMapping("/api/messages/{senderId}/{receiverId}")
	@ResponseBody
	public ResponseEntity<List<MemberChat>> getConversation(@PathVariable Integer senderId,
			@PathVariable Integer receiverId) {
		return ResponseEntity.ok(memberChatService.getConversation(senderId, receiverId));
	}

	@MessageMapping("/chat")
	@SendTo("/topic/messages")
	public MemberChatDto handleWebSocketMessage(MemberChatDto memberChatDto) throws Exception {
		MemberChat memberChat = memberChatService.saveMessage(memberChatDto.getSenderId(),
				memberChatDto.getReceiverId(), memberChatDto.getContent());
		return memberChatService.convertToDto(memberChat);
	}
	
}
