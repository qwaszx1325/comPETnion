package com.pet.controller.adopt;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pet.model.adopt.Adoptions;
import com.pet.model.adopt.Application;
import com.pet.model.member.Members;
import com.pet.service.adopt.AdoptCRUDService;
import com.pet.service.adopt.AdoptService;

import jakarta.servlet.http.HttpSession;

@Controller
@CrossOrigin
public class AdoptFrontController {

	@Autowired
	private AdoptService adoptService;
	@Autowired
	private AdoptCRUDService adoptCRUDService;
	
	// 查看單一案件/FB分享
	@GetMapping("/public/adopt/caseDetails")
	public String getCaseDetails(@RequestParam Integer petCaseId, Model model) {
		Adoptions adoptions = adoptCRUDService.getDetailByPetCaseId(petCaseId);
		model.addAttribute("adoptions", adoptions);
		return "adopt/CasePage";
	}

	// 前台搜尋全部案件
	@GetMapping("/public/adopt/allAdoptions")
	public String getAllAdoptions(@RequestParam(value = "p", defaultValue = "1") Integer pageNum, Model model) {
		Page<Adoptions> adoptions = adoptService.findByStatus(1, pageNum);
		model.addAttribute("adoptions", adoptions);
		return "adopt/SearchPage";
	}

	// 條件搜尋
	@GetMapping("/public/adopt/searchResults")
	public String searchAdoptions(@RequestParam(name = "petCity", required = false) String petCity,
			@RequestParam(name = "petKind", required = false) String petKind,
			@RequestParam(name = "petBodyType", required = false) String petBodyType, Model model) {

		List<Adoptions> adoptions = adoptCRUDService.searchAdoptions(petCity, petKind, petBodyType);

		// 僅可搜尋到已審核過的案件
		List<Adoptions> filteredAdoptions = adoptions.stream().filter(adoption -> adoption.getCaseStatus() == 1)
				.collect(Collectors.toList());

		model.addAttribute("adoptions", filteredAdoptions);
		return "adopt/SearchPage :: searchResults";
	}

	// 申請領養-確認有無登入
	@GetMapping("/member/adopt/checkLoginStatus")
	public ResponseEntity<String> checkLoginStatus(HttpSession httpSession) {
		Members member = (Members) httpSession.getAttribute("member");
		if (member == null) {
			return ResponseEntity.ok("未登入");
		}
		return ResponseEntity.ok("已登入");
	}
	
	// 申請領養-request
	@ResponseBody
	@PostMapping("/member/adopt/RequestSend")
	public ResponseEntity<?> adoptRequest(@RequestBody Application application, HttpSession httpSession) {
		Members member = (Members) httpSession.getAttribute("member");
		if (member == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入");
		}
		Integer loginUserId = member.getMemberId();

		try {
			if (adoptService.hasUserApplied(loginUserId, application.getAdoptions().getPetCaseId())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("您已申請過此案件的領養，請稍待通知！");
			}
			application.setMembersById(loginUserId);
			Application savedApplication = adoptService.applyForCase(application);
			return ResponseEntity.ok(savedApplication);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("處理申請時發生錯誤: " + e.getMessage());
		}
	}

	// 申請領養-確認有無申請過
	@ResponseBody
	@GetMapping("/member/adopt/checkApplication")
	public ResponseEntity<?> checkApplicationStatus(@RequestParam Integer petCaseId, HttpSession httpSession) {
	    Members member = (Members) httpSession.getAttribute("member");
	    if (member == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入");
	    }
	    Integer loginUserId = member.getMemberId();

	    boolean hasApplied = adoptService.hasUserApplied(loginUserId, petCaseId);
	    return ResponseEntity.ok(Collections.singletonMap("hasApplied", hasApplied));
	}
	
}
