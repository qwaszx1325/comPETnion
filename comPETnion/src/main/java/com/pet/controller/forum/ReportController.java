package com.pet.controller.forum;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pet.model.forum.Messages;
import com.pet.model.forum.Post;
import com.pet.model.forum.Report;
import com.pet.model.forum.Theme;
import com.pet.model.member.Members;
import com.pet.service.forum.ReportService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReportController {
	@Autowired
	private ReportService reportService;

	
	// 查詢檢舉全部(導入後台)
			@GetMapping("/admin/findAllReport")
			public String findAllReport(Model model) {

				List<Report> arrayList = reportService.findAllReport();
				model.addAttribute("arrayList", arrayList);

				return "/forum/reports/reportTable";
			}
	
	
	// 查詢檢舉全部使用者本人
		@GetMapping("/member/findAllReportById")
		public String findAllReportByMemberId(Model model,HttpSession httpSession) {
			Members member = (Members) httpSession.getAttribute("member");

			List<Report> arrayList = reportService.findAllReportByMemberId(member.getMemberId());
			model.addAttribute("arrayList", arrayList);

			return "/forum/MemberCenterForum";
		}
	
	
	//新增
	@PostMapping("/member/report/insert")
	private String insertReport(@RequestParam Integer postId, @RequestParam String postName,
			@RequestParam String reportContent, HttpSession httpSession, Model model) {
		// 從 session 中獲取 member 對象
		Members member = (Members) httpSession.getAttribute("member");
		System.out.println(postId);
		// 檢查 member 對象是否為 null
		if (member == null) {
			// 如果 member 為 null，說明用戶未登入，重定向到登入頁面
			return "redirect:/members/register"; // 確保你有一個處理 /login 的路由
		}
		Integer memberId = member.getMemberId();
		String  memberName = member.getMemberName();
		model.addAttribute("postId", postId);
		model.addAttribute("postName", postName);

		reportService.insertReport(postId, postName, reportContent, memberId,memberName);

		return "redirect:/" + postId + "/messages";

	}

	// 檢舉總比數
	@GetMapping("/admin/reports/countByPostId")
	@ResponseBody
	public Map<String, Long> countReportsByPostId(@RequestParam Integer postId) {
		long count = reportService.countReportsByPostId(postId);
		return Map.of("count", count);
	}

	@ResponseBody
	@GetMapping("/admin/reportPage")
	public Page<Report> findReportPage(
			@RequestParam(value = "p", defaultValue = "1") Integer pageNum) {
	    return reportService.findLatest(pageNum);
	}
	//尋找id
	@ResponseBody
	@GetMapping("/admin/findReportAllByPostId")
	public List<Report> findReportAllByPostId(
	    @RequestParam("postId") Integer postId) {
	    return reportService.findReportAllByPostId(postId);
	}
	
	//
	// 更新Status
	 @ResponseBody
	    @PostMapping("/admin/updateReportStatus")
	    public ResponseEntity<Report> updateReportStatus(@RequestParam("reportId") String reportIdStr, @RequestParam("reportState") String reportStateStr) {
//	        System.out.println("開始修改");

	        int reportId = Integer.parseInt(reportIdStr); 
	        int reportState = Integer.parseInt(reportStateStr);

	        try {
	            Report updatedReport = reportService.updateReportState(reportId, reportState);
	            return ResponseEntity.ok(updatedReport); // 返回成功消息
	        } catch (EntityNotFoundException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 返回404狀態碼
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 返回500狀態碼
	        }
	    }
	

	
//	更新Status
	
	@PostMapping("/member/deleteReportStatus")
	public String deleteReportStatus(@RequestParam("reportId")Integer reportId,@RequestParam("reportState")Integer reportState) {
              
//		    System.out.println("reportId"+reportId);
		    
	        reportService.deleteReportState(reportId,reportState);
	        return "redirect:/member/findAllReportById"; // 返回成功消息
	   

	}
}
