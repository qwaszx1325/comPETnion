package com.pet.repository.adopt;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.dto.adopt.CaseStatDto;
import com.pet.model.adopt.Adoptions;

import jakarta.transaction.Transactional;

public interface AdoptRepository extends JpaRepository<Adoptions, Integer> {

	@Query("FROM Adoptions a WHERE a.members.memberId = :memberId")
	List<Adoptions> findByMemberId(@Param("memberId") Integer memberId);

	Page<Adoptions> findByCaseStatus(int caseStatus, Pageable pageable);

	// 複雜查詢的介面: JpaSpecificationExecutor
	List<Adoptions> findAll(Specification<Adoptions> spec);

	@Modifying
	@Transactional
	@Query("UPDATE Adoptions a SET a.caseStatus = :caseStatus WHERE a.petCaseId = :petCaseId")
	void updateCaseStatus(@Param("petCaseId") Integer petCaseId, @Param("caseStatus") Integer caseStatus);

	// 圖表統計
	@Query("SELECT new com.pet.dto.adopt.CaseStatDto(" 
			+ "CONCAT(CAST(YEAR(a.petPostDate) AS string), '-', "
			+ "CASE WHEN MONTH(a.petPostDate) < 10 THEN CONCAT('0', CAST(MONTH(a.petPostDate) AS string)) "
			+ "ELSE CAST(MONTH(a.petPostDate) AS string) END) as month, " 
			+ "COUNT(a) as totalCases, "
			+ "COUNT(a) as published, " 
			+ "SUM(CASE WHEN a.caseStatus = 3 THEN 1 ELSE 0 END) as adopted) "
			+ "FROM Adoptions a " 
			+ "WHERE YEAR(a.petPostDate) = :year "
			+ "GROUP BY YEAR(a.petPostDate), MONTH(a.petPostDate)")
	List<CaseStatDto> getCaseStatsByYear(@Param("year") int year);

}
