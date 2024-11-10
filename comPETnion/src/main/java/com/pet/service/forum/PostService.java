package com.pet.service.forum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pet.model.forum.Forumphotos;
import com.pet.model.forum.Post;
import com.pet.model.forum.Theme;
import com.pet.model.member.Members;
import com.pet.repository.forum.PostRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;

	public boolean checkPostNameExistExcludingCurrent(String postName, Integer postId) {
		List<Post> posts = postRepository.findByPostNameAndPostIdNot(postName, postId);
		System.out.println("判斷信明"+ postId);
		
		// 检查是否有重复的名称
		return !posts.isEmpty();
	}

	// 確定是否有重複文章
	public boolean checkPostNameExist(String PostName) {
		List<Post> allByPostName = postRepository.findAllByPostName(PostName);
		return !allByPostName.isEmpty();
	}

	// 新增瀏覽次數
	public Post insertPostViews(Integer postId) {
		Optional<Post> optional = postRepository.findById(postId);

		if (optional.isPresent()) {
			Post post = optional.get();
			Integer postViews = post.getPostViews();
			if (postViews == null) {
				postViews = 0; // 如果為 null，設置為 0
			}
			post.setPostViews(postViews + 1); // 遊覽次數加 1
			return postRepository.save(post);
		} else {
			return null;
		}
	}

	// 假刪除
	public Post deleteState(Integer postId, Boolean postDeleteState) {
		Optional<Post> optionalPost = postRepository.findById(postId);
		if (optionalPost.isPresent()) {
			Post post = optionalPost.get();
			post.setPostDeleteState(postDeleteState);
			return postRepository.save(post);
		}
		return null;
	}

	// 修改狀態
	public Post updatePostShowStatus(Integer postId, Boolean postShow) {
		Optional<Post> optional = postRepository.findById(postId);
		if (optional.isPresent()) {
			Post post = optional.get();
			post.setPostShow(postShow);

			return postRepository.save(post);
		} else {
			return null; // or handle the case where Theme is not found
		}
	}

	// 當前所有主題的文章
	public List<Post> findPostByThemeId(Integer themeId) {
		return postRepository.findPostByThemeId(themeId);
	}

	// 分頁 當themeId下的前12筆
	public Page<Post> findByPage(Integer themeId, Integer pageNumber) {
		Pageable pageable = PageRequest.of(pageNumber - 1, 12, Sort.Direction.DESC, "postDate");
		return postRepository.findByThemeThemeId(themeId, pageable);
	}

	// 分頁 查詢 當themeId的前12筆最多的遊覽次數
	public Page<Post> findPostViewsByPage(Integer themeId, Integer pageNumber) {
		Pageable pageable = PageRequest.of(pageNumber - 1, 12, Sort.Direction.DESC, "postViews");
		return postRepository.findByThemeThemeId(themeId, pageable);
	}

	// 分頁 查詢全部的最新前12筆的
	public Page<Post> findAllByPage(Integer pageNumber) {
		Pageable pageable = PageRequest.of(pageNumber - 1, 12, Sort.Direction.DESC, "postDate");
		return postRepository.findAllPageByPost(pageable);
	}

	// 新增

	public Post savePost(Post post) {
		System.out.println("有到嗎");
		return postRepository.save(post);
	}

	// 修改
//	@Transactional
//	public Post updatePost(Boolean postShow,String memberName,Integer memberId,String postContent, String postName, Integer postId, MultipartFile[] files, Theme themeId) {
//	    System.out.println("Service: " + postId);
//	  
//	    // 處理圖片上傳
//	    if (files != null && files.length > 0) {
//	        List<Forumphotos> forumPhotosList = new ArrayList<>();
//
//	        for (MultipartFile file : files) {
//	            if (!file.isEmpty()) {
//	                try {
//	                    Forumphotos forumPhoto = new Forumphotos();
//	                    forumPhoto.setPhotoFile(file.getBytes());
//	                    forumPhoto.setPost(post);
//	                    forumPhotosList.add(forumPhoto);
//	                } catch (IOException e) {
//	                    throw new RuntimeException("Failed to process uploaded file", e);
//	                }
//	            }
//	        }
//
//	        post.setForumphotos(forumPhotosList);
//	    }
//
//	    return postRepository.save(post);
//	}

	// 模糊查詢
	public List<Post> findAllPost(String post) {
		return postRepository.findAllByPostName(post);
	}

	// 查詢id
	public Post findById(Integer id) {
		Optional<Post> optional = postRepository.findById(id);
		// isPresent檢查Optional是否為空直
		if (!optional.isPresent()) {
			return null;
		}

		return optional.get();

	}

	// 刪除
	public void deleteByPostId(Integer postId) {
		postRepository.deleteById(postId);

	}

	// 分頁
	public Post findLastestMsg() {
		Pageable pgb = PageRequest.of(0, 1, Sort.Direction.DESC, "postDate");
		Page<Post> page = postRepository.findLatest(pgb);

		List<Post> resultList = page.getContent();
		if (resultList.isEmpty()) {
			return null;
		}

		return resultList.get(0);
	}

	public List<Post> findAll() {
		return postRepository.findAll();
	}

}
