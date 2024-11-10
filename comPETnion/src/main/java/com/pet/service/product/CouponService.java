package com.pet.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.model.member.Members;
import com.pet.model.product.Coupon;
import com.pet.model.product.MemberCoupon;
import com.pet.repository.member.MembersRepository;
import com.pet.repository.product.CouponRepository;
import com.pet.repository.product.MemberCouponRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {
    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MemberCouponRepository memberCouponRepository;

    @Autowired
    private MembersRepository memberRepository;

    public Coupon addCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public List<Coupon> getCouponsByMemberId(Integer memberId) {
        List<MemberCoupon> memberCoupons = memberCouponRepository.findByMember_MemberId(memberId);
        return memberCoupons.stream()
                .map(MemberCoupon::getCoupon)
                .collect(Collectors.toList());
    }

    public boolean assignCouponToMember(Integer memberId, Integer couponId) {
        Members member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + memberId));
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new IllegalArgumentException("Invalid coupon Id:" + couponId));

        if (!memberCouponRepository.existsByMemberAndCoupon(member, coupon)) {
            MemberCoupon memberCoupon = new MemberCoupon();
            memberCoupon.setMember(member);
            memberCoupon.setCoupon(coupon);
            memberCouponRepository.save(memberCoupon);
            return true;
        }
        return false;
    }

    public void removeCouponFromMember(Integer memberId, Integer couponId) {
        Members member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + memberId));
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new IllegalArgumentException("Invalid coupon Id:" + couponId));

        memberCouponRepository.deleteByMemberAndCoupon(member, coupon);
    }

    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }
    
    public void deleteCoupon(Integer couponId) {
        couponRepository.deleteById(couponId);
    }
    
    public Coupon updateCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }
}
