package com.pet.controller.member;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pet.model.member.Members;
import com.pet.service.member.MembersService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MembersController {

	@Autowired
	private MembersService membersService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String showHomePage(HttpSession session, Model model) {
		Members member = (Members) session.getAttribute("member");
		model.addAttribute("member", member);
		return "index";

	}

	@GetMapping("/public/login")
	public String login() {
		return "member/memberLogin";
	}

	@PostMapping("/public/login")
	public String loginMember(@RequestParam("memberEmail") String memberEmail,
			@RequestParam("memberPassword") String memberPassword, Model model, 
			HttpSession httpSession,RedirectAttributes redirectAttributes) {
		
		System.out.println("登入密碼:"+memberPassword);
		Members member = membersService.checkLogin(memberEmail, memberPassword);
		
//		System.out.println("登入加密密碼:"+member.getMemberPassword());

		if (member != null) {
			switch(member.getMemberstatusId()) {
	    	case 0://未驗證
	    	case 1://正常
	    
	    		httpSession.setAttribute("member", member);
	            model.addAttribute("alertLogin", "success");
	            model.addAttribute("member", member);
	            //httpSession.removeAttribute("admin");//刪除admin session
	            return "member/getMember";
	            
	    	case 2://封鎖
	    		redirectAttributes.addFlashAttribute("alertLogin", "blocked");
	            redirectAttributes.addFlashAttribute("errorMessage", "您的帳戶已被封鎖，無法登入。如有疑問，請聯繫客服。");
	    		return "redirect:/public/login";
		}
	}
		 // 帳號或密碼錯誤
	    redirectAttributes.addFlashAttribute("alertLogin", "failure");
	   // redirectAttributes.addFlashAttribute("errorMessage", "帳號或密碼錯誤");
	    return "redirect:/public/login";
	}

	@GetMapping("/public/register")
	public String registerMemberMain() {
		return "member/memberLogin";
	}

	@PostMapping("/public/register")
	public String registerMember(@RequestParam("memberEmail") String memberEmail,
			@RequestParam("memberName") String memberName, @RequestParam("memberPassword") String memberPassword,
			@RequestParam("memberAge") String memberAge, @RequestParam("memberPhone") String memberPhone,
			@RequestParam("memberAddress") String memberAddress, Model model,
			@RequestParam(value = "memberImg", required = false) MultipartFile mf, HttpSession httpSession)
			throws IllegalStateException, IOException, MessagingException {

		
		System.out.println("註冊密碼:"+memberPassword);
		boolean exists = membersService.checkMemberEmailExist(memberEmail);
		Members member = new Members();

		if (exists) {
			model.addAttribute("alert", "alreadyRegistered");
			return "member/memberLogin";

		} else {

			String fileName = mf.getOriginalFilename();
			byte[] b = mf.getBytes();
			
			if (fileName != null && fileName.length() != 0) {
				member.setMemberImg(b);
			} else {
				InputStream defaultImageStream = getClass().getResourceAsStream("/static/imgs/default.png");
				if (defaultImageStream != null) {
					byte[] defaultImageBytes = defaultImageStream.readAllBytes();
					member.setMemberImg(defaultImageBytes);
				}
			}

			// 從用戶端獲取生日--再轉換成年紀

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");// 看變數是--還是//
			LocalDate date = LocalDate.parse(memberAge, formatter);
			LocalDate currentDate = LocalDate.now();
			int age = date.until(currentDate).getYears();
			String ageAsString = String.valueOf(age);

			member.setMemberAge(ageAsString);
			member.setMemberAddress(memberAddress);
			member.setMemberEmail(memberEmail);
			member.setMemberName(memberName);
			member.setMemberstatusId(0);
			member.setVerified(false);

			// 生成驗證令牌
			String token = UUID.randomUUID().toString();
			member.setVerificationToken(token);
			member.setTokenExpiryDate(LocalDateTime.now().plusMinutes(30)); // 令牌30分鐘後過期

			// members.getMemberPassword()是用户在前端输入的参数。
			// passwordEncoder.encode(password) 方法進行加密（編碼），生成一個加密後的密碼字符串，並將其存儲在 encodedPwd
			// 變量中。
			// System.out.println(memberPassword);
			String encodedPwd = passwordEncoder.encode(memberPassword);
			
			//System.out.println("註冊加密密碼:"+encodedPwd);
			member.setMemberPassword(encodedPwd);

			member.setMemberPhone(memberPhone);
			membersService.registerMember(member);

			// 發送驗證郵件
			membersService.sendVerificationEmail(member.getMemberEmail(), token);

			// 註冊會員時還為未生成ID
			// 註冊有輸入email，且無重複，才能註冊成功
			// controller跟html共通的點是email，所以用email抓member資料
			// 因為session貫穿整個瀏覽器，所以設一個屬性方便後續使用
			// model是供下一個html使用
			// member是下一個頁面取出的值，selectMemberByEmail是我的變數
			// 這邊的程式是為了註冊完直接登入以及會員顯示圖片方便使用
			Members selectMemberByEmail = membersService.selectMemberByEmail(memberEmail);
			//httpSession.setAttribute("member", selectMemberByEmail);
			model.addAttribute("member", selectMemberByEmail);
			model.addAttribute("alert", "registeredSuccess");
			//return "member/getMember";
			return "member/registrationSuccess";

		}
	}

	// 處理郵箱驗證請求
	@GetMapping("/public/verify")
	public String verifyEmail(@RequestParam String token, Model model) {
		boolean verified = membersService.verifyEmail(token);
		Members member = new Members();
		if (verified) {
			 model.addAttribute("message", "您的電子郵件已成功驗證！");
		
		} else {
			model.addAttribute("message", "驗證失敗，請檢查您的驗證鏈接。");
		}
		return "member/verificationResult";
	}

	@GetMapping("/member/updatemembermain")
	public String updateMemberMain(Model model, @RequestParam("memberEmail") String memberEmail) {

		Members member = membersService.selectMemberByEmail(memberEmail);
		model.addAttribute("member", member);

		return "member/updateMember";
	}
	

	@PostMapping("/member/updatemember")
	public String updateMember(@RequestParam("memberEmail") String memberEmail,
			@RequestParam("memberName") String memberName, 
			@RequestParam("memberPassword") String memberPassword,
			@RequestParam("memberAge") String newMemberAge, 
			@RequestParam("memberPhone") String memberPhone,
			@RequestParam("memberAddress") String memberAddress, 
			@RequestParam("originalImg") String originalImg,
			Model model, 
			@RequestParam(value = "memberImg", required = false) MultipartFile mf,
			@RequestParam("memberId") Integer memberId, 
			@RequestParam("oldMemberAge") String oldMemberAge,
			@RequestParam("memberstatusId") Integer memberstatusId, HttpSession session) throws IllegalStateException, IOException {
		
		Members members = new Members();
		// 取得目前登入會員的資料
		Members member = (Members) session.getAttribute("member");

		members.setMemberId(memberId);
		members.setMemberName(memberName);
		
		if(member.getMemberPassword().equals(memberPassword)) {
			// 如果登入的會員的密碼(加密過的)等於 前台加密過的密碼 代表 會員根本沒改帳密，直接存進資料庫原本加密的密碼
			members.setMemberPassword(memberPassword);	
		}else {
			// 如果目前登入的會員的密碼(加密過的) 如果 不等於 前台加密過的密碼，代表會員有改密碼，所以把它密碼加密存進資料庫			
			String encodedPwd = passwordEncoder.encode(memberPassword);
			members.setMemberPassword(encodedPwd);
		}
		
		members.setMemberPhone(memberPhone);
		members.setMemberEmail(memberEmail);
		members.setMemberAddress(memberAddress);
		members.setMemberstatusId(memberstatusId);
//		System.out.println(memberEmail);

		if (mf != null && !mf.isEmpty()) {
			byte[] b = mf.getBytes();
			members.setMemberImg(b);

		} else {

			Members oldMember = membersService.selectMemberByEmail(memberEmail);
			if (oldMember != null && oldMember.getMemberImg() != null) {
				members.setMemberImg(oldMember.getMemberImg());
			}
		}

		// 檢查是否提供了新的生日信息
		if (newMemberAge == null || newMemberAge.isEmpty()) {
			members.setMemberAge(oldMemberAge);
			// 沒有提供新的生日，使用原來的生日
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");// 看變數是--還是//
			LocalDate date = LocalDate.parse(newMemberAge, formatter);
			LocalDate currentDate = LocalDate.now();
			int age = date.until(currentDate).getYears();
			String ageAsString = String.valueOf(age);
			members.setMemberAge(ageAsString);
		}
		membersService.saveMembers(members);
		session.setAttribute("member", members);
		model.addAttribute("memberId", members.getMemberId());
		model.addAttribute("member", members);
		model.addAttribute("alert", "updateSuccess");
		return "member/getMember";
	}

	//顯示一個人的頁面
	@GetMapping("/member/getonememberdata")
	public String getOneMemberData(@RequestParam("memberEmail") String memberEmail, Model model) {

		Members member = membersService.selectMemberByEmail(memberEmail);
		model.addAttribute("member", member);

		return "member/updateMember";
	}

//	@GetMapping("/members/memberfunction")
//	public String memberfunction(@RequestParam("memberEmail") String memberEmail, Model model) {
//		Members member = membersService.selectMemberByEmail(memberEmail);
//		model.addAttribute("member", member);
//		return "member/memberFunction";
//
//	}

	
	//登入取使用者session資料
	@GetMapping("/member/getmember")
	public String getmember(HttpSession httpSession, Model model) {
		Members member = (Members) httpSession.getAttribute("member");
		model.addAttribute("member", member);
		return "member/getMember";
	}
	
	//簽到用ajax
	@ResponseBody
	@PostMapping("/member/daily/checkIn")
	public Integer dailyCheckIn(HttpSession httpSession) {
		Members member = (Members) httpSession.getAttribute("member");
		Integer memberId = member.getMemberId();
		boolean signInStatus = membersService.dailyCheckIn(memberId);
		if(signInStatus) {
			Integer consecutiveCheckIns = membersService.getConsecutiveCheckIns(memberId);
			return consecutiveCheckIns;
		}
		return 0;
	}

	@GetMapping("/member/download")
	public ResponseEntity<byte[]> downloadPhotos(@RequestParam Integer memberId) {
		Members members = membersService.findPhotosById(memberId);

		byte[] photoFile = members.getMemberImg();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		// body, headers , http status code
		return new ResponseEntity<byte[]>(photoFile, headers, HttpStatus.OK);
	}

	@GetMapping("/member/logout")
	public String logout(HttpSession httpSession) {

		httpSession.invalidate();
		System.out.println("already log out");

		return "redirect:/";
	}
	
	//註冊頁面ajax資料驗證
	@GetMapping("/public/check/email")
	@ResponseBody
    public ResponseEntity<?> checkEmail(@RequestParam String memberEmail) {
        boolean exists = membersService.checkMemberEmailExist(memberEmail);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
	
	
}

