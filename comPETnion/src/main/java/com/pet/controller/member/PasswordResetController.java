package com.pet.controller.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pet.model.member.Members;
import com.pet.service.member.ForgotPasswordService;
import jakarta.mail.MessagingException;
@Controller
public class PasswordResetController {

	    @Autowired
	    private ForgotPasswordService forgotPasswordService;

		@GetMapping("/public/forgot/password")
	    public String showForgotPasswordForm() {
	        
	        return "member/forgotPassword";
	    }
	    
	    
	 
	    @PostMapping("/public/forgot/password")
	    public String processForgotPassword(@RequestParam("memberEmail") String memberEmail, Model model) throws MessagingException {
	    	// 調用會員服務處理忘記密碼請求，並獲取處理結果
	        boolean isProcessed = forgotPasswordService.processForgotPassword(memberEmail);
	        
	        if (isProcessed) {
	           
	            model.addAttribute("message", "密碼重置連結已發送到您的信箱");
	        } else {
	            // 如果處理失敗（可能是因為找不到會員），設置錯誤消息
	            model.addAttribute("error", "找不到該信箱對應的會員");
	        }

	        return "member/forgotPasswordResult"; 
	    }
	    

	    @GetMapping("/public/reset/password")
	    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
	        // 驗證重置令牌
	        Members member = forgotPasswordService.validateResetToken(token);
	        if (member == null) {
	            // 如果令牌無效，設置錯誤消息並返回錯誤頁面
	            model.addAttribute("error", "無效或已過期的重置連結");
	            return "error";
	        }
	        // 將有效的令牌添加到模型中
	        model.addAttribute("token", token);	   
	        return "member/resetPassword";
	    }
	    
	   
	    @PostMapping("/public/reset/password")
	    public String processResetPassword(@RequestParam("token") String token, 
	                                       @RequestParam("newMemberPassword") String newMemberPassword,
	                                       Model model) {
	        // 再次驗證重置令牌
	        Members member = forgotPasswordService.validateResetToken(token);
	        if (member == null) {
	            // 如果令牌無效，設置錯誤消息並返回錯誤頁面
	            model.addAttribute("error", "無效或已過期的重置連結");
	            return "error";
	        }
	        
	        // 重置密碼
	        forgotPasswordService.resetPassword(member, newMemberPassword);
	        
	        model.addAttribute("message", "密碼重置成功");

	        return "member/resetPasswordResult";
	    }

}
