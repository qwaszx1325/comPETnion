package com.pet.controller.adopt;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pet.dto.adopt.AdoptDto;
import com.pet.model.adopt.Adoptions;
import com.pet.model.adopt.Application;
import com.pet.model.member.Members;
import com.pet.service.adopt.AdoptCRUDService;
import com.pet.service.adopt.AdoptService;
import com.pet.utils.SendMail;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdoptBackController {

	@Autowired
	private AdoptService adoptService;
	@Autowired
	private AdoptCRUDService adoptCRUDService;
	@Autowired
	private SendMail sendMail;

	// 新增案件
	@GetMapping("/member/adopt/addCase")
	public String addPetCase(HttpSession httpSession, Model model) {
		Members member = (Members) httpSession.getAttribute("member");
		if (member == null) {
			return "redirect:/member/members/login";
		}
		model.addAttribute("adoptDto", new AdoptDto());
		return "adopt/AddPetCasePage";
	}

	@PostMapping("/member/adopt/caseSubmit")
	public String showAllPetCase(@ModelAttribute("adoptDto") AdoptDto adoptDto, HttpSession httpSession, Model model) {
		Members member = (Members) httpSession.getAttribute("member");
		if (member != null) {
			adoptDto.setMemberId(member.getMemberId());
			adoptCRUDService.createPetCase(adoptDto);
			List<Adoptions> adoptions = adoptCRUDService.getAllAdoptions();
			model.addAttribute("myAdoptions", adoptions);
			return "redirect:/member/adopt/myPetCase";
		} else {
			return "redirect:/member/members/login";
		}
	}

	// 顯示圖片
	@GetMapping("/adopt/api/showPhoto")
	public ResponseEntity<?> showPetPhoto(@RequestParam Integer id) {
		return adoptService.downloadPetPhoto(id);
	}

	// 更新案件內容
	@GetMapping("/member/adopt/editCase")
	public String editPetCase(@RequestParam Integer petCaseId, Model model) {
		Adoptions adoptions = adoptCRUDService.getDetailByPetCaseId(petCaseId);
		model.addAttribute("adoptions", adoptions);
		return "adopt/UpdateCasePage";
	}

	@PutMapping("/member/adopt/editResult")
	public String editResult(@ModelAttribute AdoptDto adoptDto,
			@RequestParam(value = "petPhoto", required = false) MultipartFile[] files,
			@RequestParam(value = "deletePhotoIds", required = false) List<Integer> deletePhotoIds) {
		adoptDto.setPetPhotoFile(files);
		adoptDto.setDeletePhotoIds(deletePhotoIds);
		adoptCRUDService.updatePetCase(adoptDto);
		return "redirect:/member/adopt/myPetCase";
	}

	// 假刪除案件（狀態更改成ADOPTED）
	@DeleteMapping("/member/adopt/caseAdopted")
	public String changeCaseStatus(@RequestParam Integer petCaseId) {
		adoptCRUDService.changeToAdoptedById(petCaseId);
		return "redirect:/member/adopt/myPetCase";
	}

	// 查看自己全部案件
	@GetMapping("/member/adopt/myPetCase")
	public String findMyPetCase(HttpSession httpSession, Model model) {
		Members member = (Members) httpSession.getAttribute("member");
		Integer memberId = member.getMemberId();
		List<Adoptions> adoptions = adoptCRUDService.findByMemberId(memberId);
		model.addAttribute("myAdoptions", adoptions);
		return "adopt/MyAllPetCase";
	}

	// 查看所申請之領養案件
	@GetMapping("/member/adopt/myApplication")
	public String getMyApplication(HttpSession httpSession, Model model) {
		Members member = (Members) httpSession.getAttribute("member");
		Integer memberId = member.getMemberId();

		List<Application> myApplication = adoptCRUDService.findMyApplication(memberId);
		model.addAttribute("myApplication", myApplication);
		return "adopt/MyApplication";
	}

	// 取得申請審核資料
	@GetMapping("/member/adopt/getApplication")
	public ResponseEntity<List<Application>> getApplicationsByPetCaseId(@RequestParam Integer petCaseId) {
		List<Application> applications = adoptService.getApplicationsByPetCaseId(petCaseId);
		return ResponseEntity.ok(applications);
	}

	// 申請之審核選擇 response
	@ResponseBody
	@PostMapping("/member/adopt/select")
	public ResponseEntity<String> selectApplication(@RequestBody Map<String, Integer> request) {
		Integer applyId = request.get("applyId");
		if (applyId == null) {
			return ResponseEntity.badRequest().body("無此領養申請");
		}
		try {
			Map<String, Object> applicationInfo = adoptService.selectApplication(applyId);

			if (applicationInfo != null) {
				String applicantEmail = (String) applicationInfo.get("applicantEmail");
				Integer petCaseId = (Integer) applicationInfo.get("petCaseId");
				sendMail.sendApplicationResponse(applicantEmail, petCaseId);
				return ResponseEntity.ok("選擇成功，通知已發送");
			} else {
				return ResponseEntity.badRequest().body("選擇失敗，該申請可能已被選擇或狀態更新失敗");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("選擇處理失敗：" + e.getMessage());
		}
	}

	@GetMapping("/member/adopt/applicationStatus")
	public ResponseEntity<Map<String, Object>> getApplicationStatus(@RequestParam Integer applyId) {
		Map<String, Object> status = adoptService.getApplicationStatus(applyId);
		return ResponseEntity.ok(status);
	}
	
	// 取消申請案件
	@DeleteMapping("/member/adopt/cancelApplication")
	public String cancelApplication(@RequestParam Integer applyId) {
		adoptService.cancelApplication(applyId);
		return "redirect:/member/adopt/myApplication";
	}

}
