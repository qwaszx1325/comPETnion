package com.pet.controller.forum;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pet.model.forum.Theme;
import com.pet.service.forum.ThemeService;

@Controller
public class ThemeController {

	@Autowired
	private ThemeService themeService;

	// id查詢
	@GetMapping("/member/findById")
	@ResponseBody
	public Theme findById(@RequestParam Integer id) {
		return themeService.findById(id); // 假設返回的是主題描述
	}

//	更新Status
	@ResponseBody
	@PostMapping("/admin/updateThemeShowStatus")
	public Theme updateThemeShowStatus(@RequestBody Theme theme) {

		return themeService.updateShowStatus(theme.getThemeId(), theme.getIsShow());
	}

	@PostMapping("/admin/updateThemeDataAjax")
	@ResponseBody
	public Map<String, String> updateThemeDataAjax(@RequestParam("themeId") Integer themeId,
			@RequestParam(value = "forumPhoto", required = false) MultipartFile forumPhoto,
			@RequestParam("themeName") String themeName, @RequestParam("themeDescription") String themeDescription) {

		Map<String, String> response = new HashMap<>();

		// 檢查是否存在同名主題（排除當前主題）
		if (themeService.checkThemeNameExistExcludingCurrent(themeName, themeId)) {
			response.put("status", "error");
			response.put("message", "已經有此主題，請重新輸入");
		} else {
			try {
				themeService.update(themeId, forumPhoto, themeName, themeDescription);
				response.put("status", "success");
				response.put("message", "修改成功");
			} catch (Exception e) {
				response.put("status", "error");
				response.put("message", "修改失敗: " + e.getMessage());
			}
		}
		return response;
	}
  //新增主題
	@PostMapping("/admin/insertThemeDataAjax")
	@ResponseBody
	public Map<String,  String> insert(@RequestParam MultipartFile forumPhoto,
			@RequestParam(name = "themeName") String themeName,
			@RequestParam("themeDescription") String themeDescription) throws IllegalStateException, IOException {

		Map<String, String> response = new HashMap<>();

		// 检查主题名称是否重复
		boolean checkThemNameExist = themeService.checkThemNameExist(themeName);

		if (checkThemNameExist) {
			response.put("status", "error");
			response.put("message", "已经有此主题，请重新输入");
		} else {
			// 插入新主题
			themeService.insert(forumPhoto, themeName, themeDescription);
			response.put("status", "success");
			response.put("message", "新增成功");
		}

		// 返回 JSON 数据
		return response;
	}

	// 新增跳轉頁面
	@GetMapping("/member/forum/insertTheme2")
	public String insertTheme2() {
		return "/forum/Theme/insertTheme.html";

	}

	// 顯示主頁
	@GetMapping("/findAllTheme")
	public String findAllTheme(Model model) {

		List<Theme> arrayList = themeService.findAllByIsShow();
		model.addAttribute("arrayList", arrayList);
		return "forum/indexFrontForum";
//		return "/forum/blog";
	}

	// 後臺查詢全部
	@GetMapping("/admin/findAllTheme2")
	public String findAllTheme2(Model model) {

		List<Theme> arrayList = themeService.findAllback();
		model.addAttribute("arrayList", arrayList);

		return "/forum/Theme/GetAllTheme";
	}

	// 模糊查詢
	@PostMapping("/member/findByNameTheme")
	public String findByNameTheme(@RequestParam("themeName") String themeName, Model model) {
		List<Theme> arrayList = themeService.findTheme(themeName);
		model.addAttribute("arrayList", arrayList);

		return "/forum/indexForum";
//		return "/forum/blog";
	}



	@GetMapping("/member/deleteTheme")
	public String deleteTheme(@RequestParam("id") Integer id) {
		themeService.deleteById(id);

		return "redirect:/public/adminlogin/findAllTheme2";
	}

	@GetMapping("/member/findByThemeIdUpdata")
	private String findByThemeIdUpdata(Model model, @RequestParam("id") Integer id) {

		Theme themeBean = themeService.findById(id);

		model.addAttribute("themeBean", themeBean);

		return "forum/Theme/updateTheme";

	}

	@PostMapping("/admin/updateTheme")
	public String updateTheme(@RequestParam("themeId") Integer themeId, @RequestParam MultipartFile forumPhoto,
			@RequestParam("themeName") String themeName, @RequestParam("themeDescription") String themeDescription,
			Model model) throws IllegalStateException, IOException {

		themeService.update(themeId, forumPhoto, themeName, themeDescription);

		return "redirect:/admin/findAllTheme2";
	}

	@GetMapping("/forumPhoto")
	public ResponseEntity<byte[]> downloadPhotos(@RequestParam Integer themeId) {
		Theme forumPhoto = themeService.findById(themeId);

		byte[] photoFile = forumPhoto.getForumPhoto();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		// body, headers , http status code
		return new ResponseEntity<byte[]>(photoFile, headers, HttpStatus.OK);
	}

}
