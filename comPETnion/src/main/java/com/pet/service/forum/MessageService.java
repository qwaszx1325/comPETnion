package com.pet.service.forum;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.pet.model.adopt.Adoptions;
import com.pet.model.forum.Messages;
import com.pet.model.forum.Post;
import com.pet.model.forum.Theme;
import com.pet.model.member.Members;
import com.pet.repository.forum.MessagesRepository;

import jakarta.servlet.http.HttpSession;



@Service
public class MessageService {
	
	@Autowired
	private MessagesRepository messagesRepository;
	
	
	
	   //編輯
	   public Messages editMessage( Integer messagesId,String messageContent) {
	        // 查找留言
	        Messages message = messagesRepository.findById(messagesId).orElseThrow(() -> new RuntimeException("Message not found"));
	        // 更新留言内容
	        message.setMessageContent(messageContent);
	        
	        // 保存更新后的留言
	        messagesRepository.save(message);
	        // 重定向到原页面
			return  messagesRepository.save(message);
	       
	    }
	
	
	
	
	// 藉由獲取資料（前台會員）
	public List<Messages> findByMemberId(Integer memberId) {
		 List<Messages> byMemberId = messagesRepository.findByMemberId(memberId);
		return byMemberId;
	}
	
	
	//假刪除
		public Messages updateMessagesShowStatus(Integer messagesId, Boolean messagesShow) {
		    Optional<Messages> optional = messagesRepository.findById(messagesId);
		    if (optional.isPresent()) {
		        Messages messages = optional.get();
		        messages.setMessageShow(messagesShow);
		        System.out.println(messagesShow);
		        return messagesRepository.save(messages);
		    } else {
		        return null; 
		    }
		}
		
		
		
		
		//修改狀態
				public Messages updateMessageDeleteState(Integer messagesId, Boolean messageDeleteState) {
				    Optional<Messages> optional = messagesRepository.findById(messagesId);
				    if (optional.isPresent()) {
				        Messages messages = optional.get();
				        messages.setMessageShow(messageDeleteState);
				        System.out.println(messageDeleteState);
				        return messagesRepository.save(messages);
				    } else {
				        return null; // or handle the case where Theme is not found
				    }
				}
			
	
	
	
	//當前所有文章的訊息
	 public List<Messages> getMessagesByPostId(Integer postId) {
	        return messagesRepository.findMessagesByPostId(postId);
	    }
	
	//當前文章新增訊息
	 public Messages insert(String postName,Integer postId, String messageContent, HttpSession httpSession) {
		   
		  Members member = (Members) httpSession.getAttribute("member");
		 Messages message = new Messages();
		Post post = new Post();
		   
		    post.setPostId(postId);
		    
		    message.setMessageContent(messageContent);
		    message.setPost(post);
		    message.setMessageShow(true);
		    message.setMessageDeleteState(true);
		    message.setMemberName(member.getMemberName());
		    message.setMemberId(member.getMemberId());
		    message.setPostName(postName);
		   
//         System.out.println(postName);
		    // 保存並返回新消息
		    return messagesRepository.save(message);
		}
	
	
	public Messages findMagById(Integer id) {
 Optional<Messages> optional = messagesRepository.findById(id);
		//isPresent檢查Optional是否為空直
		if(!optional.isPresent()) {
			return null;
		}
		
		return optional.get();
		
	}
	
	 public List<Messages> getAllMessages() {
	        return messagesRepository.findAll();
	    }
	
	public void deleteMsgById(Integer id) {
		messagesRepository.deleteById(id);
	}
	
	public Messages findLastestMsg() {
		Pageable pgb = PageRequest.of(0, 1, Sort.Direction.DESC, "messageDate");
		Page<Messages> page = messagesRepository.findLatest(pgb);
		
		List<Messages> resultList = page.getContent();
		//isEmpty檢查陣列,及何等是否為空直
		if(resultList.isEmpty()) {
			return null;
		}
		
		return resultList.get(0);
	}
	
	
	//分頁
	public Page<Messages> findByPage(Integer pageNumber){
		Pageable pgb = PageRequest.of(pageNumber-1, 3, Sort.Direction.DESC, "messageDate");
		Page<Messages> page = messagesRepository.findAll(pgb);
		return page;
	}
	
	// 新增一筆後，回傳最新的前三筆
	public Page<Messages> addOneAndReturnThree(Messages messages){
		messagesRepository.save(messages);
		
		Pageable pgb = PageRequest.of(0, 3, Sort.Direction.DESC, "messageDate");
		
		Page<Messages> page = messagesRepository.findAll(pgb);
		
		return page;
	}


    //新增
	  public Messages save(Messages messages) {
		return messagesRepository.save(messages);
	  }
		// 新增一筆後，回傳最新全部
		public List<Messages> addOneAndReturnMessages(Messages messages){
			messagesRepository.save(messages);
			Post post = new Post();
			
			
			List<Messages> messagesByPostId = messagesRepository.findMessagesByPostId(post.getPostId());
			
			return messagesByPostId ;
		}


		
	

}
