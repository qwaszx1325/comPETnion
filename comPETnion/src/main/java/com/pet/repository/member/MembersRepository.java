package com.pet.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.member.Members;

import jakarta.transaction.Transactional;

import java.util.List;

public interface MembersRepository extends JpaRepository<Members, Integer> {

	@Query("FROM Members")
	List<Members> findAllMembers();

	// HQL語法
	Members findByMemberName(String MemberName);

	Members findByMemberEmail(String memberEmail);

	@Modifying
	@Transactional
	@Query("UPDATE Members SET memberstatusId =:memberstatusId WHERE memberId = :memberId")
	public void updateMembersStatus(@Param("memberstatusId") Integer memberstatusId,
			@Param("memberId") Integer memberId);

	Members findByVerificationToken(String token); // 通過驗證令牌查找會員

	// 根據重置密碼令牌查找會員
	Members findByResetPasswordToken(String token);

}
