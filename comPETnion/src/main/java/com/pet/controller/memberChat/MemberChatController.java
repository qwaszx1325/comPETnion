package com.pet.controller.memberChat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

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
	
	@MessageMapping("/chat")
	@SendTo("/topic/messages")
	public MemberChatDto handleWebSocketMessage(MemberChatDto memberChatDto) throws Exception {
		MemberChat memberChat = memberChatService.saveMessage(memberChatDto.getSenderId(),
				memberChatDto.getReceiverId(), memberChatDto.getContent());
		return memberChatService.convertToDto(memberChat);
	}
	
	@GetMapping("/member/chatList")
	public String chatList(Model model, HttpSession httpSession) {
	    Members member = (Members) httpSession.getAttribute("member");
	    if (member == null) {
	        return "redirect:/member/members/login";
	    }

	    List<MemberChatDto> conversations = memberChatService.getConversationList(member.getMemberId());
	    model.addAttribute("conversations", conversations);
	    model.addAttribute("currentUserId", member.getMemberId());
	    model.addAttribute("currentUserName", member.getMemberName());

	    return "memberChat/memberChat";
	}

	@GetMapping("/member/chat/{receiverId}")
    public String chatView(@PathVariable Integer receiverId, Model model, HttpSession httpSession) {
        Members member = (Members) httpSession.getAttribute("member");
        if (member == null) {
            return "redirect:/member/members/login";
        }
        Members receiver = membersService.findMembersById(receiverId);

        if (receiver == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found");
        }

        // 獲取或創建對話
        List<MemberChatDto> conversation = memberChatService.getOrCreateConversation(member.getMemberId(), receiverId);

        // 獲取對話列表
        List<MemberChatDto> conversations = memberChatService.getConversationList(member.getMemberId());

        model.addAttribute("currentUserId", member.getMemberId());
        model.addAttribute("currentUserName", member.getMemberName());
        model.addAttribute("receiverId", receiverId);
        model.addAttribute("receiverName", receiver.getMemberName());
        model.addAttribute("conversations", conversations);
        model.addAttribute("currentConversation", conversation);

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
	public ResponseEntity<List<MemberChatDto>> getConversation(@PathVariable Integer senderId,
			@PathVariable Integer receiverId) {
		return ResponseEntity.ok(memberChatService.getConversation(senderId, receiverId));
	}
}