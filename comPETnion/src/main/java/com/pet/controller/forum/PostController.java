package com.pet.controller.forum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pet.model.forum.Forumphotos;
import com.pet.model.forum.Post;
import com.pet.model.forum.Report;
import com.pet.model.forum.Theme;
import com.pet.model.member.Members;
import com.pet.service.forum.PostService;
import com.pet.service.forum.ReportService;
import com.pet.service.forum.ThemeService;

import java.text.SimpleDateFormat;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class PostController {

	public static final String UPLOAD_DIR = "uploads"; // 確保這個路徑根據你的需求進行設置
//	public static final long MAX_FILE_SIZE = 2000 * 1024; // 2000KB

	
	@Autowired
	private PostService postService;

	@Autowired
	private ThemeService themeService;
	
	
	private ReportService reportService;

//	更新Status
	@ResponseBody
	@PostMapping("/admin/updatePostShowStatus")
	public Post updatePostShowStatus(@RequestBody Post post) {
	
		return postService.updatePostShowStatus(post.getPostId(), post.getPostShow());
	}
	
//	更新Status
	@ResponseBody
	@PostMapping("/admin/reportSuccessful")
	public Post reportSuccessful(@RequestBody Post post) {
//	System.out.println("開始囉");	
//	System.out.println("postId"+post.getPostId());
//	System.out.println( "PostShow"+post.getPostShow());
		return postService.updatePostShowStatus(post.getPostId(), post.getPostShow());
	}
	
	
//	假刪除
	
	@PostMapping("/member/postDeleteState")
	public String postDeleteState(@RequestParam Integer postId) {
		System.out.println(postId);
	    Boolean postDeleteState = false; // 設置為 true 表示帖子被"刪除"
	    Post updatedPost = postService.deleteState(postId, postDeleteState);
	    if (updatedPost != null) {
	        // 可以添加成功消息
	        return "redirect:/findAllTheme";
	    } else {
	        // 可以添加失敗消息
	        return "redirect:/error";
	    }
	}

	//真刪除
		@GetMapping("/member/deletePost")
		public String deletePost(@RequestParam("id") Integer id) {
			postService.deleteByPostId(id);

			return "redirect:/member/findAllPost";
		}
	

	// 顯示當前主題文章
	@GetMapping("/findPostBythemeId")
	public String findPostBythemeId(@RequestParam("themeId") Integer themeId,
			@RequestParam(value = "p", defaultValue = "1") Integer pageNum, Model model, HttpSession session) {

		// 更新遊覽次數 每次跳轉該主題遊覽次數+1
		themeService.insertViews(themeId);

		
		List<Post> posts = postService.findPostByThemeId(themeId);
		Page<Post> page = postService.findByPage(themeId, pageNum); // 分頁
		
		model.addAttribute("posts", posts);
		model.addAttribute("page", page);
		model.addAttribute("themeId", themeId); // 将themeId传递给前端

		return "forum/post/indexForumPost";
	}

	// 文章發布與圖片上傳
	@PostMapping("/member/post/publish")
	public String publish(@RequestParam("postContent") String postContent,
	                      @RequestParam("postName") String postName,
	                      @RequestParam("themeId") Integer themeId,
	                      @RequestParam("files") MultipartFile[] files,
	                      HttpSession httpSession,
	                      Model model) throws IOException {
	    // 從 session 中獲取 member 對象
	    Members member = (Members) httpSession.getAttribute("member");

	    // 檢查 member 對象是否為 null
	    if (member == null) {
	        // 如果 member 為 null，說明用戶未登入，重定向到登入頁面
	        return "redirect:/members/register";
	    }

	    // 檢查主題名稱是否重複
//	    boolean checkPostNameExist = postService.checkPostNameExist(postName);
//
//	    if (checkPostNameExist) {
//	        // 如果主題名稱已存在，返回錯誤訊息給前端
//	        return "redirect:/member/insertPostNoOK";
//	    } else {
	    
	    
//	        // 主題名稱不存在，可以進行文章發佈流程
	        Theme theme = themeService.findById(themeId);

	        // 建立一個新的文章物件
	        Post post = new Post();
	        post.setPostShow(true);
	        post.setPostDeleteState(true);
	        
	        
	        post.setPostContent(postContent);
	        
	        
	        post.setPostDate(new Date());
	        post.setPostName(postName);
	        post.setTheme(theme);
	        post.setMemberName(member.getMemberName());
	        post.setMemberId(member.getMemberId());

	        // 如果有上傳的圖片，處理圖片上傳
	        if (files != null && files.length > 0) {
	            List<Forumphotos> forumPhotosList = new ArrayList<>();

	            for (MultipartFile file : files) {
	                Forumphotos forumPhoto = new Forumphotos();
	                forumPhoto.setPhotoFile(file.getBytes());
	                forumPhoto.setPost(post); // 設置圖片對應的文章
	                forumPhotosList.add(forumPhoto);
	            }

	            post.setForumphotos(forumPhotosList); // 將圖片列表設置到文章中
	        }

	        model.addAttribute("postPublished", true);

	        Post savedPost =postService.savePost(post); // 儲存文章到資料庫
	        Integer postId = savedPost.getPostId();	        // 發佈成功後重定向到成功頁面
	        return "redirect:/"+postId+"/messages";
//	    }
	}



	// 導入後台
	@GetMapping("/admin/findAllPost")
	public String findAllPost(Model model) {
		List<Post> arrayList = postService.findAll();
		model.addAttribute("arrayList", arrayList);
		
		
		return "forum/post/postTable";
	}

	// 查詢名稱
	@PostMapping("/member/findByNamePost")
	public String findByNamePodt(@RequestParam("postName") String postName, Model model) {

		List<Post> arrayList = postService.findAllPost(postName);
		System.out.println(arrayList);

		model.addAttribute("arrayList", arrayList);

		return "forum/post/GetAllPost";
	}


	// 新增成功文章 連TinyMCE.html
	@GetMapping("/member/insertPostOk")
	public String insertPostOk(Model model) {
		  model.addAttribute("insertOK", "新增文章成功");
		List<Theme> arrayList = themeService.findAll();
		model.addAttribute("arrayList", arrayList);
		return "forum/post/TinyMCE";
	}
	// 重複文章主題連TinyMCE.html
	@GetMapping("/member/insertPostNoOK")
	public String insertPostNoOK(Model model) {
		  model.addAttribute("insertError", "已經有此主題，請重新輸入");
		List<Theme> arrayList = themeService.findAll();
		model.addAttribute("arrayList", arrayList);
		return "forum/post/TinyMCE";
	}
	// 跳轉TinyMCE.html
	@GetMapping("/member/post/insertPost")
	public String insertPost(Model model) {
		
		List<Theme> arrayList = themeService.findAll();
		model.addAttribute("arrayList", arrayList);
		return "forum/post/TinyMCE";
	}
//  




	

	@GetMapping("/post/page")
	public String findByPage(@RequestParam(value = "p", defaultValue = "1") Integer pageNum, Model model) {

		Page<Post> page = postService.findAllByPage(pageNum);

		model.addAttribute("page", page);
		return "forum/post/indexForumPost";
//			return "forum/post/listPostPageAjax";
	}
	

	@GetMapping("/member/postViewsbypage")
	public String findAllByPage(@RequestParam(value = "p", defaultValue = "1") Integer pageNum,@RequestParam (value = "t") Integer themeId, Model model) {

		

		// 更新遊覽次數 每次跳轉該主題遊覽次數+1
		themeService.insertViews(themeId);

		List<Post> posts = postService.findPostByThemeId(themeId);
		Page<Post> page = postService.findPostViewsByPage(themeId, pageNum);

		model.addAttribute("posts", posts);
		model.addAttribute("page", page);
		model.addAttribute("themeId", themeId); // 将themeId传递给前端

		return "forum/post/indexForumPost";
	}
	
	
	
	

	// 尋找修改ID
	@GetMapping("/member/findIdByPost")
	public String editMsg(@RequestParam Integer id, Model model) {
		Post post = postService.findById(id);
		model.addAttribute("post", post);
		List<Theme> arrayList = themeService.findAll();
		model.addAttribute("arrayList", arrayList);
		return "forum/post/updatePage";

	}
	
	
	// 跳轉編輯頁面
		@GetMapping("/member/findupdatePage")
		public String editPost(Model model) {
		
		
			return "forum/post/updatePage";

		}

		
		//編輯文章
	@PostMapping("/member/update")
	public String updatePost(@RequestParam("postContent") String postContent,
	                         @RequestParam("postName") String postName,
	                         @RequestParam("themeId") Integer themeId,
	                         @RequestParam("postId") Integer postId,
	                         HttpSession httpSession,
	                         Model model) {

	    System.out.println("開始更新文章: " + postId);

	    // 從 session 中獲取 member 對象
	    Members member = (Members) httpSession.getAttribute("member");

	    // 檢查 member 對象是否為 null
	    if (member == null) {
	        return "redirect:/members/login";
	    }

	    try {
	        // 從數據庫中獲取現有的 post
	        Post existingPost = postService.findById(postId);
	        if (existingPost == null) {
	            model.addAttribute("error", "找不到指定的文章");
	            return "redirect:/member/findupdatePage";
	        }

	        // 檢查主題名稱是否重複，排除當前文章
	        boolean checkPostNameExist = postService.checkPostNameExistExcludingCurrent(postName, postId);
	        if (checkPostNameExist) {
	            model.addAttribute("error", "文章標題已存在，請使用其他標題");
	            model.addAttribute("post", existingPost);
	            model.addAttribute("arrayList", themeService.findAll());
	            return "forum/post/updatePage";
	        }

	        // 獲取Theme對象
	        Theme theme = themeService.findById(themeId);

	        // 只更新這三個字段
	        existingPost.setPostContent(postContent);
	        existingPost.setPostName(postName);
	        existingPost.setTheme(theme);

	        // 保存更新後的文章
	        postService.savePost(existingPost);

	        model.addAttribute("success", "文章編輯成功");
	        return "redirect:/"+postId+"/messages"; // 重定向到文章列表頁面
	    } catch (Exception e) {
	        model.addAttribute("error", "文章編輯失敗: " + e.getMessage());
	        return "forum/post/updatePage";
	    }
	}
}




