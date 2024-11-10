package com.pet.controller.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pet.model.admin.Admin;
import com.pet.model.member.Members;
import com.pet.service.admin.AdminService;
import com.pet.service.member.MembersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserLoginController {

//	@Autowired
//	private MembersService membersService;
//
//	@Autowired
//	private AdminService adminService;
//
//	@GetMapping("/public/login")
//	public String login() {
//		return "member/memberLogin";
//	}
//	
//	@PostMapping("/public/login")
//	public String loginUser(@RequestParam("memberEmail") String email, @RequestParam("memberPassword") String password,
//			Model model, HttpSession httpSession, RedirectAttributes redirectAttributes) {
//		Admin admin = adminService.checkLogin(email, password);
//		if(admin!=null) {
//			 model.addAttribute("loginSuccess", "登入成功");
//		        httpSession.setAttribute("admin", admin);
//		        httpSession.setAttribute("loginAdminId", admin.getAdminId());
//		        httpSession.setAttribute("loginAdminEmail", admin.getAdminEmail());
//		        
//		        List<Members> members = membersService.findAllMember();
//		        model.addAttribute("members", members);
//		        httpSession.removeAttribute("member");
//		        return "admin/indexBackAdmin";
//		}
//		// 如果不是管理員 則檢查是否為會員
//	    Members member = membersService.checkLogin(email, password);
//	    if (member != null) {
//	    	switch(member.getMemberstatusId()) {
//	    	case 0://未驗證
//	    	case 1://正常
//	    	case 3://解鎖
//	    		httpSession.setAttribute("member", member);
//	            model.addAttribute("alertLogin", "success");
//	            model.addAttribute("member", member);
//	            httpSession.removeAttribute("admin");//刪除admin session
//	            return "member/getMember";
//	            
//	    	case 2://封鎖
//	    		redirectAttributes.addFlashAttribute("alertLogin", "blocked");
//	            redirectAttributes.addFlashAttribute("errorMessage", "您的帳戶已被封鎖，無法登入。如有疑問，請聯繫客服。");
//	    		return "redirect:/public/login";
////	    	default:
////                redirectAttributes.addFlashAttribute("alertLogin", "error");
////                redirectAttributes.addFlashAttribute("errorMessage", "帳戶狀態異常，請聯繫客服。");
////                return "redirect:/public/login";
//	    	}
//	    }
//	    // 帳號或密碼錯誤
//	    redirectAttributes.addFlashAttribute("alertLogin", "failure");
//	    redirectAttributes.addFlashAttribute("errorMessage", "帳號或密碼錯誤");
//	    return "redirect:/public/login";
//	}
//
//	
	
	
	


//	@PostMapping("/public/login")
//	public String loginUser(@RequestParam("memberEmail") String email, @RequestParam("memberPassword") String password,
//			Model model, HttpSession httpSession, RedirectAttributes redirectAttributes) {
//
//		Members member = membersService.checkLogin(email, password);
//		if (member != null) {
//			if(member.getMemberstatusId()==2) {
//				redirectAttributes.addFlashAttribute("alertLogin", "blocked");
//	            redirectAttributes.addFlashAttribute("errorMessage", "您的帳戶已被封鎖，無法登入。如有疑問，請聯繫客服。");
//	            return "redirect:/public/login";
//			}else {
//			
//			httpSession.setAttribute("member", member);
//			model.addAttribute("alertLogin", "success");
//			model.addAttribute("member", member);
//			httpSession.removeAttribute("admin");
//			return "member/getMember";
//		}
//		Admin result = adminService.checkLogin(email, password);
//		if (result != null) {
//			model.addAttribute("loginSuccess", "登入成功");
//			httpSession.setAttribute("admin", result);
//			httpSession.setAttribute("loginAdminId", result.getAdminId());
//			httpSession.setAttribute("loginAdminEmail", result.getAdminEmail());
//			
//			List<Members> members = membersService.findAllMember();
//	
//	        model.addAttribute("members", members);
//	        httpSession.removeAttribute("member");
//	        return "admin/indexBackAdmin";
//		}
//		
//	}
//		redirectAttributes.addFlashAttribute("alertLogin", "failure");
//		redirectAttributes.addFlashAttribute("errorMessage", "帳號或密碼錯誤");
//		return "redirect:/public/login";
//}
}