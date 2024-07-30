package com.pet.repository.forum;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.forum.Post;
import com.pet.model.forum.Report;



public interface ReportRepository extends JpaRepository<Report, Integer> {
	
	
	  //文章下所有的檢舉
	  @Query("FROM Report r WHERE r.postId = :postId and r.reportState = 0" )
	    List<Report> findReportByPostId(@Param("postId") Integer postId);

	  
	  //查詢postId總共有幾筆
	  @Query("SELECT COUNT(r) FROM Report r WHERE r.postId = :postId and r.reportState = 0")
	  long countByPostId(@Param("postId") Integer postId);
	  
	  //文章下所有的檢舉
	  @Query("FROM Report r WHERE r.memberId = :memberId")
	    List<Report> findReportByMemberId(@Param("memberId") Integer memberId);
	  
	  
	  
		//分頁 and r.reportState = 0
	  @Query("from Report")
	    Page<Report> findLatest(Pageable pageable);
		
	  
}
