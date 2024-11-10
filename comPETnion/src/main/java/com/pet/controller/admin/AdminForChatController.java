package com.pet.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.pet.dto.admin.CustomerChatMessageByUserName;
import com.pet.service.admin.CustomerChatMessageService;

@Controller
public class AdminForChatController {

	@Autowired
private CustomerChatMessageService customerChatMessageService;
	
	
	@GetMapping("/member/CustomerChatMessage")
	public String CustomerChatMessage() {
		return "/member/CustomerChatMessagePage";
	}
	
	//客服(admin)
		@GetMapping("/admin/CustomerChatMessage")
		public String CustomerChatMessage(Model m) {
			List<CustomerChatMessageByUserName> userList = customerChatMessageService.getUsersWithAdminChats("admin");
	        m.addAttribute("userList", userList);
			return "admin/CustomerChatMessageByAdmin";
		}
}
