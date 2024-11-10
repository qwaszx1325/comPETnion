package com.pet.repository.adopt;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.adopt.Application;

import jakarta.transaction.Transactional;


public interface ApplicationRepository extends JpaRepository<Application, Integer> {
	
	
	List<Application> findByMembersMemberId(Integer memberId);
	
    List<Application> findByAdoptionsPetCaseId(Integer petCaseId);
    
    Application findByApplyId(Integer applyId);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM Application a WHERE a.members.memberId = :memberId AND a.adoptions.petCaseId = :petCaseId")
    boolean userHasApplied(@Param("memberId") Integer memberId, @Param("petCaseId") Integer petCaseId);
    
    @Query("SELECT a FROM Application a WHERE a.applyTime < :date AND a.ownerRespond = false")
    List<Application> findOldPendingApplications(@Param("date") Date date);
    
    @Modifying
	@Transactional
	@Query("UPDATE Application a SET a.applyStatus = :applyStatus WHERE a.applyId = :applyId")
	void updateApplyStatus(@Param("applyId") Integer applyId, @Param("applyStatus") String applyStatus);
    
}

