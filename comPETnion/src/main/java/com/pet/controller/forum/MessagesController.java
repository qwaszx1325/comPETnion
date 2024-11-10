package com.pet.controller.forum;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pet.model.forum.Messages;
import com.pet.model.forum.Post;
import com.pet.model.member.Members;
import com.pet.service.forum.MessageService;
import com.pet.service.forum.PostService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MessagesController {

	@Autowired
	private MessageService messageService;

	
	@Autowired
	private PostService postService;
	
	
	
	
//	更新Status 用AJAX
	@ResponseBody
	@PostMapping("/admin/updateMessagesShowStatus")
	public Messages updateMessagesShowStatus(@RequestBody Messages messages) {
//System.out.println(messages.getMessagesId());
	    return messageService.updateMessagesShowStatus(messages.getMessagesId(), messages.getMessageShow());
	}
	

  //假刪除
	@PostMapping("/member/updateMessageDeleteState")
	public String updateMessageDeleteState(@RequestParam Integer messagesId,@RequestParam Integer postId) {
    
		Boolean  MessageDeleteState = false;
     messageService.updateMessageDeleteState(messagesId, MessageDeleteState);
     return "redirect:/" + postId + "/messages";
	}


	
	
	
	//導入後台
		@GetMapping("/admin/findAllmessage")
		public String findAllmessage(Model model) {
			
			 List<Messages> arrayList = messageService.getAllMessages();
 
			model.addAttribute("arrayList", arrayList);

			return "forum/messages/messagesTable";
		}
	
	
	//顯示訊息當文章下
	 @GetMapping("/{postId}/messages")
	    public String getMessagesByPostId(@PathVariable Integer postId, Model model,HttpSession httpSession) {
		 
		
		
		 
		Post post = postService.findById(postId);
		String postName = post.getPostName();
	        List<Messages> messages = messageService.getMessagesByPostId(postId);
	      //訪問該問遊覽次數+1
			postService.insertPostViews(postId);
	        model.addAttribute("post", post);
	        model.addAttribute("messages", messages);
	        model.addAttribute("postId", postId);
	        model.addAttribute("postName", postName);
	        return "forum/post/postContent";
	    }
	 
        //新增訊息
	 @PostMapping("/member/addPost")
	 public String createMessage(Post post, @RequestParam String messageContent, HttpSession httpSession) {
	     Members member = (Members) httpSession.getAttribute("member");
	     Integer postId = post.getPostId();
	     String postName = post.getPostName();
	     if (member == null) {
	         // 错误回登入画面
	         return "redirect:/members/register"; 
	     }

	     try {
	         // 调用 service 方法并传递 member 对象
	         messageService.insert(postName, postId, messageContent, httpSession);
	         return "redirect:/" + postId + "/messages";
	     } catch (IllegalArgumentException e) {
	         // 错误回登入画面
	         return "redirect:/members/register"; 
	     }
	 }
      //編輯留言
	    @PostMapping("/member/editMessage")
	    public String editMessage(@RequestParam Integer postId,@RequestParam Integer messagesId, @RequestParam String messageContent) {
	    
	        // 保存更新后的留言
	        messageService.editMessage(messagesId,messageContent);
	       
	        return "redirect:/" + postId + "/messages";
	    }
	



}
