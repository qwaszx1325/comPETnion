package com.pet.controller.admin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pet.model.admin.Admin;
import com.pet.model.member.Members;
import com.pet.service.admin.AdminService;
import com.pet.service.member.MembersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private MembersService membersService;

	@GetMapping("/public/adminlogin")
	public String login() {
		return "admin/loginPage";
	}

//	@GetMapping("/admin/loginPost")
//	public String loginAdmin( HttpSession httpSession, Model model) {
//		
//		
//		List<Members> members = membersService.findAllMember();
//		//System.out.println(members);
//        model.addAttribute("members", members);
//
//		return "admin/indexBackAdmin";
//	}
	@PostMapping("/public/adminlogin")
	public String loginAdmin(@RequestParam("adminEmail") String adminEmail,
			@RequestParam("adminPassword") String adminPassword, HttpSession httpSession, Model model) {

		Admin result = adminService.checkLogin(adminEmail, adminPassword);

		if (result != null) {
			model.addAttribute("loginSuccess", "登入成功");
			httpSession.setAttribute("admin", result);
			httpSession.setAttribute("loginAdminId", result.getAdminId());
			httpSession.setAttribute("loginAdminEmail", result.getAdminEmail());

			List<Members> members = membersService.findAllMember();
			model.addAttribute("members", members);
			//httpSession.removeAttribute("member");

			return "admin/indexBackAdmin";

		} else {
			model.addAttribute("alertLoginFalse", "登入失敗");
			return "admin/loginPage";
		}
	}

	@GetMapping("/admin/logout")
	public String logout(HttpSession httpSession) {
//		httpSession.invalidate();
		httpSession.removeAttribute("admin");
		return "admin/loginPage";
	}

//	@GetMapping("/admin/delete")
//	public String deleteMemberById(@RequestParam("memberId") UUID memberId) {
//		membersService.deleteMember(memberId);	
//		return "redirect:/admin/getallmemberdata";
//	}
//	
	@GetMapping("/admin/update")
	public String updateMemberById(@RequestParam("memberstatusId") Integer memberstatusId,
			@RequestParam("memberId") Integer memberId) {
		// System.out.println(1);
		membersService.updateMembersStatus(memberstatusId, memberId);
		return "redirect:/admin/getallmemberdata";
	}

	@GetMapping("/admin/getallmemberdata")
	// 把model想像成request
	public String getAllMemberData(Model model) {
		List<Members> allMembers = membersService.findAllMember();
		model.addAttribute("members", allMembers);
		return "admin/indexBackAdmin";
	}

	@GetMapping("/admin/adminindex")
	public String adminFunction() {

		return "admin/indexBackAdmin";

	}

//	//0710
//	@GetMapping("/admin/getmemberdata")
//	public String getmemberById(@RequestParam("memberId") Integer memberId,Model model) {
//		Members membersById = membersService.findMembersById(memberId);
//		model.addAttribute("member",membersById);
//		return "member/getMemberByAdmin";
//	}

//	@GetMapping("/admin/getmemberdata")
//	@ResponseBody
//	public String getmemberById(@RequestParam("memberId") Integer memberId) {
//		Members member = membersService.findMembersById(memberId);
//		return "<div>會員照片:"+member.getMemberImg()+"</div>"+
//				"<div>會員姓名:" +member.getMemberName()+"</div>"+
//		        "<div>會員帳號:"+member.getMemberEmail()+ "</div>" +
//		        "<div>會員年紀:"+member.getMemberAge()+"</div>"+
//		        "<div>會員電話:"+member.getMemberPhone()+"</div>"+
//		        "<div>會員地址:"+member.getMemberAddress()+"</div>";
//	}
	

	
	@GetMapping("/admin/getmemberdata")
	@ResponseBody
	public Members getmemberById(@RequestParam("memberId") Integer memberId) {
		Members member = membersService.findMembersById(memberId);
		return member;
	}
	

	@GetMapping("/admin/download")
	public ResponseEntity<byte[]> downloadPhotos(@RequestParam Integer memberId) {
		Members members = membersService.findPhotosById(memberId);

		byte[] photoFile = members.getMemberImg();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		// body, headers , http status code
		return new ResponseEntity<byte[]>(photoFile, headers, HttpStatus.OK);
	}

}
