package com.pet.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.order.MonthlyOrderTotalsWithMembers;

public interface MonthlyOrderTotalsWithMembersRepository extends JpaRepository<MonthlyOrderTotalsWithMembers, String>{

//	public List<MonthlyOrderTotalsWithMembers> findByMemberId(Integer memberId);
	@Query(value = "SELECT * FROM MonthlyOrderTotalsWithMembers WHERE memberId = :memberId", nativeQuery = true)
    List<MonthlyOrderTotalsWithMembers> findByMemberId(@Param("memberId") Integer memberId);
}
