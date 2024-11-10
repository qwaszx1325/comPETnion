package com.pet.repository.forum;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.forum.Post;
import com.pet.model.forum.Theme;


public interface PostRepository extends JpaRepository<Post, Integer> {

	
	  List<Post> findByPostNameAndPostIdNot(String postName, Integer postId);
	
	// 模糊查詢全部
	@Query("from Post where postName like %:postName% and postShow = true and postDeleteState  = true")
	List<Post> findAllByPostName(@Param(value = "postName") String postName);

	 //是否顯示
	@Query("from Post where postShow = true and postDeleteState  = true")
	List<Post> findAllByPostShow();

	
	//分頁
	@Query("from Post where postShow = true and postDeleteState  = true ")
	Page<Post> findLatest(Pageable pgb);

	@Query("FROM Post p WHERE p.postId = :postId and p.postShow = true and postDeleteState  = true")
	List<Post> findByPostId(@Param("postId") Integer postId);
	
   
    //themeId下的文章
	@Query("FROM Post p WHERE p.theme.themeId = :themeId and p.postShow = true and postDeleteState  = true")
	List<Post> findPostByThemeId(@Param("themeId") Integer themeId);
    
    // 分頁查themeId下的文章
	@Query("FROM Post p WHERE p.theme.themeId = :themeId and p.postShow = true and postDeleteState = true")
	Page<Post> findByThemeThemeId(@Param("themeId") Integer themeId, Pageable pageable);
	
	@Query("from Post where postShow = true and postDeleteState = true")
	Page<Post> findAllPageByPost(Pageable pageable);
    
  //+1遊覽次數
  		Post save(Integer views);
  		
  	//判定資料庫是否有此文章
  			Post findByPostName(String PostName);
 
}
