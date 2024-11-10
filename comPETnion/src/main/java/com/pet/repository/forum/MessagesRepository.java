package com.pet.repository.forum;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.adopt.Adoptions;
import com.pet.model.forum.Messages;


public interface MessagesRepository extends JpaRepository<Messages, Integer> {

	@Query("FROM Messages m WHERE m.messageShow = true and messageDeleteState = true")
	Page<Messages> findLatest(Pageable pgb);

	//文章當前留言
	@Query("FROM Messages m WHERE m.post.postId = :postId AND m.messageShow = true and messageDeleteState = true")
	List<Messages> findMessagesByPostId(@Param("postId") Integer postId);

	//查詢會員ID
	List<Messages> findByMemberId(Integer memberId);
	
	@Query("FROM Messages m WHERE m.memberId = :memberId AND m.post.postId = :postId AND m.messageShow = true and messageDeleteState = true")
	List<Messages> findMessagesByPostIdAndMemberId(Integer memberId, Integer postId);
}
