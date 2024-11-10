package com.pet.service.forum;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.model.forum.Post;
import com.pet.model.forum.Report;
import com.pet.repository.forum.ReportRepository;

@Service
public class ReportService {

	
	@Autowired
	private ReportRepository reportRepository;

	
	
	
	// 新增檢舉
	public Report insertReport(Integer postId, String postName, String reportContent, Integer memberId,String memberName) {
		Report report = new Report();
		report.setMemberId(memberId);
		report.setPostId(postId);
		report.setPostName(postName);
		report.setReportContent(reportContent);
		report.setReportDate(new Date());
		report.setReportState(0);
		report.setMemberName(memberName);

		return reportRepository.save(report);
	}

	// 查詢全部
	public List<Report> findAllReport() {
		return reportRepository.findAll();
	}
	// 查詢全部
		public List<Report> findAllReportByMemberId(Integer memberId) {
			return reportRepository.findReportByMemberId(memberId);
		}
	

	// 修改檢舉狀態
    @Transactional
	public Report updateReportState(Integer reportId,Integer reportState) {
    	 Optional<Report> optional = reportRepository.findById(reportId);
    	    if (optional.isPresent()) {
    	        Report report = optional.get(); // 獲取現有報告
    	        report.setReportState(reportState);
    	        System.out.println(report); // 查看报告对象的状态
    	        
    	        try {
    	            return reportRepository.save(report); // 保存更新後的報告
    	        } catch (Exception e) {
    	            System.err.println("Error while saving report: " + e.getMessage());
    	            e.printStackTrace(); // 输出异常堆栈跟踪
    	        }
    	    } else {
    	        System.err.println("Report not found for id: " + reportId);
    	        return null; // 或者你可以拋出一個自定義異常
    	    }
    	    System.out.println("沒成功");
    	    return null; // 如果保存失败，返回 null
	}
    
    @Transactional
 	public Report deleteReportState( Integer reportId,Integer reportState) {
     	 Optional<Report> optional = reportRepository.findById(reportId);
     	    if (optional.isPresent()) {
     	        Report report = optional.get(); // 獲取現有報告
     	        report.setReportState(5);
//     	        System.out.println(report); // 查看报告对象的状态
     	        
     	        try {
     	            return reportRepository.save(report); // 保存更新後的報告
     	        } catch (Exception e) {
     	            System.err.println("Error while saving report: " + e.getMessage());
     	            e.printStackTrace(); // 输出异常堆栈跟踪
     	        }
     	    } else {
     	        System.err.println("Report not found for id: " + reportId);
     	        return null; // 或者你可以拋出一個自定義異常
     	    }
     	    System.out.println("沒成功");
     	    return null; // 如果保存失败，返回 null
 	}
    
    
	//posId總共有幾筆
	 public long countReportsByPostId(Integer postId) {
	        return reportRepository.countByPostId(postId);
	    }

	// 分頁查询全部的最舊1条
	    public Page<Report> findLatest(Integer pageNumber) {
	        Pageable pageable = PageRequest.of(pageNumber - 1, 1, Sort.Direction.ASC, "reportDate");
	        return reportRepository.findLatest( pageable);
	 }
	 // 查詢文章下所有的檢舉
		public List<Report> findReportAllByPostId(Integer postId) {
			return reportRepository.findReportByPostId(postId);
		}
	 

	 
}
