package com.pet.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.member.Members;
import com.pet.model.product.Coupon;
import com.pet.model.product.MemberCoupon;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Integer> {
	//// 根據會員查找所有的 MemberCoupon 記錄
	List<MemberCoupon> findByMember(Members member);

	// 根據會員和優惠券刪除 MemberCoupon 記錄
	void deleteByMemberAndCoupon(Members member, Coupon coupon);

	// 判斷會員是否已經擁有該優惠券
	boolean existsByMemberAndCoupon(Members member, Coupon coupon);

	// 查找某會員的所有優惠券
	List<MemberCoupon> findByMember_MemberId(Integer memberId);
}