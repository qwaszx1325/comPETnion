package com.pet.controller.adopt;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
	public void exportToCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		// 檔名加上日期（取得當天日期並格式化）
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String formattedDate = today.format(formatter);
		String fileName = "adoptions_" + formattedDate + ".csv";

		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

		try (PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
			writer.write('\ufeff'); // Byte Order Mark (BOM): 協助Excel識別正確文件編碼

			// 設置各欄位標頭名稱
			writer.println("會員編號,領養案件編號,寵物小名,所在縣市,區域,種類,品種,毛色,性別,體型,年齡,醫療紀錄,寵物描述,案件發布日期,目前狀態(0待審核/1已上架/2審核駁回/3已被領養)");

			List<Adoptions> adoptions = adoptCRUDService.getAllAdoptions();
			for (Adoptions adoption : adoptions) {
				writer.println(String.join(",", adoption.getMembers().getMemberId().toString(),
						adoption.getPetCaseId().toString(), adoption.getPetName(), adoption.getPetCity(),
						adoption.getPetDistrict(), adoption.getPetKind(), adoption.getPetBreed(),
						adoption.getPetColour(), adoption.getPetSex(), adoption.getPetBodyType(), adoption.getPetAge(),
						adoption.getMedicalRecord(), adoption.getPetDescription(), adoption.getPetPostDate().toString(),
						adoption.getCaseStatus().toString()));
			}
		}
	}

	// 輸出Json
	@GetMapping("/adopt/api/exportJson")
	public void exportToJSON(HttpServletResponse response) throws IOException {
		response.setContentType("application/json; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		// 檔名加上日期（取得當天日期並格式化)
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String formattedDate = today.format(formatter);
		String fileName = "adoptions_" + formattedDate + ".json";
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

		List<Adoptions> adoptions = adoptCRUDService.getAllAdoptions();

		// 使用 ObjectMapper 來建立 JSON 字串
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		try (PrintWriter writer = new PrintWriter(
				new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
			writer.write('\ufeff');

			for (Adoptions adoption : adoptions) {
				// 使用 LinkedHashMap 保持欄位順序
				LinkedHashMap<String, Object> adoptionMap = new LinkedHashMap<>();
				adoptionMap.put("會員編號", adoption.getMembers().getMemberId().toString());
				adoptionMap.put("領養案件編號", adoption.getPetCaseId().toString());
				adoptionMap.put("寵物小名", adoption.getPetName());
				adoptionMap.put("所在縣市", adoption.getPetCity());
				adoptionMap.put("區域", adoption.getPetDistrict());
				adoptionMap.put("種類", adoption.getPetKind());
				adoptionMap.put("品種", adoption.getPetBreed());
				adoptionMap.put("毛色", adoption.getPetColour());
				adoptionMap.put("性別", adoption.getPetSex());
				adoptionMap.put("體型", adoption.getPetBodyType());
				adoptionMap.put("年齡", adoption.getPetAge());
				adoptionMap.put("醫療紀錄", adoption.getMedicalRecord());
				adoptionMap.put("寵物描述", adoption.getPetDescription());
				adoptionMap.put("案件發布日期", adoption.getPetPostDate().toString());
				adoptionMap.put("目前狀態(0待審核/1已上架/2審核駁回/3已被領養)", adoption.getCaseStatus().toString());

				writer.println(mapper.writeValueAsString(adoptionMap));
			}
		}
	}
}
