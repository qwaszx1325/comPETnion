package com.pet.controller.forum;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.pet.model.forum.Forumphotos;
import com.pet.service.forum.PhotoService;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.logging.Level;


@Controller
public class PhotosController {
	
	private static final String UPLOAD_DIR = "uploads";
    private static final long MAX_FILE_SIZE = 2000 * 1024; // 2000KB in bytes

	@Autowired
	private PhotoService photoService;
	
	
	
	 @GetMapping("/forum/showPhotos")
	    public ResponseEntity<byte[]> showPhotos2(@RequestParam("postId") Integer postId) {
	        try {
	            // 根据 postId 查询对应的图片数据或路径
	            Forumphotos forumphoto = photoService.getPhotoDataByPostId(postId);

	            if (forumphoto != null && forumphoto.getPhotoFile() != null) {
	                HttpHeaders headers = new HttpHeaders();
	                headers.setContentType(MediaType.IMAGE_JPEG); // 设置响应内容类型为图片类型
	                return new ResponseEntity<>(forumphoto.getPhotoFile(), headers, HttpStatus.OK);
	            } else {
	                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 找不到对应的图片数据
	            }
	        } catch (Exception e) {
	            // 处理异常情况，比如找不到图片等
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	
	@GetMapping("/member/photos/upload")
	public String upload() {
		return "photos/uploadPage";
	}
	
	
	@PostMapping("/member/photos/uploadPost")
	public String uploadPost( @RequestParam MultipartFile file, Model model) throws IOException {
		
		Forumphotos forumphotos = new Forumphotos();
		forumphotos.setPhotoFile(file.getBytes());
		
		photoService.insertPhoto(forumphotos);
		
		model.addAttribute("okMsg", "上傳成功");
		
		return "photos/uploadPage";
	}
	

	
	
	
	
	


	
	
	
	public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 檢查檔案是否為空
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }

            // 生成唯一的檔案名稱
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // 創建目標路徑
            Path targetLocation = Paths.get(UPLOAD_DIR).resolve(fileName);

            // 將檔案保存到目標位置
            Files.copy(file.getInputStream(), targetLocation);

            // 構建檔案的URL或路徑
            String fileUrl = "/uploads/" + fileName; // 假設你有一個映射到上傳目錄的URL

            // 返回成功響應，包含檔案位置
            return ResponseEntity.ok().body("{\"location\": \"" + fileUrl + "\"}");

        } catch (IOException ex) {
            // 處理異常情況
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload file: " + ex.getMessage());
        }
    }

	
	@GetMapping("/member/photos/download2")
	public ResponseEntity<byte[]> downloadPhotos(@RequestParam Integer id) {
		Forumphotos photos = photoService.findPhotosById(id);
		
		byte[] photoFile = photos.getPhotoFile();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		                                 // body,   headers , http status code
		return new ResponseEntity<byte[]>(photoFile, headers, HttpStatus.OK);	
	}
	
	@GetMapping("/member/photos/showPhotos")
	public String showPhotos(Model mode) {
		List<Forumphotos> allPhotos = photoService.findAllPhotos();
		mode.addAttribute("allPhotos", allPhotos);
		return "photos/showPage";
	}
	

}
