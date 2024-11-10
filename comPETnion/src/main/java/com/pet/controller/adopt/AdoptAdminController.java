package com.pet.controller.adopt;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pet.dto.adopt.CaseStatDto;
import com.pet.model.adopt.Adoptions;
import com.pet.service.adopt.AdoptCRUDService;
import com.pet.service.adopt.AdoptService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdoptAdminController {

	@Autowired
	private AdoptService adoptService;

	@Autowired
	private AdoptCRUDService adoptCRUDService;

	// 管理員查看全部案件
	@GetMapping("/admin/adopt/checkPage")
	public String checkPage(HttpSession httpSession, Model model) {
		List<Adoptions> adoptions = adoptCRUDService.getAllAdoptions();
		model.addAttribute("adoptions", adoptions);
		return "adopt/CheckCasePage";
	}

	// 管理員審核案件更新
	@GetMapping("/admin/adopt/checkAdoptions")
	public String checkAdoptions(@RequestParam("petCaseId") Integer petCaseId,
			@RequestParam("caseStatus") int caseStatus, HttpSession httpSession, Model model) {
		adoptService.checkPetPost(petCaseId, caseStatus);
		return "redirect:/admin/adopt/checkPage";
	}

	// 圖表頁面
	@GetMapping("/admin/adopt/chart")
	public String showChart() {
		return "adopt/adoptChart";
	}

	// 圖表-以年份分類 查看整年數據
	@GetMapping("/adopt/api/statistics")
	@ResponseBody
	public ResponseEntity<List<CaseStatDto>> getCaseStatsByYear(@RequestParam int year) {
		List<CaseStatDto> stats = adoptService.getCaseStatsByYear(year);
		return ResponseEntity.ok(stats);
	}

	// 輸出csv
	@GetMapping("/adopt/api/exportCsv")
	public void exportToCSV(HttpServletResponse response, @RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month) throws IOException {
		adoptService.exportToCSV(response, year, month);
	}

	// 輸出json
	@GetMapping("/adopt/api/exportJson")
	public void exportToJSON(HttpServletResponse response, @RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month) throws IOException {
		adoptService.exportToJSON(response, year, month);
	}

}
