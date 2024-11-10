package com.pet.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.pet.model.member.Members;

@Entity
@Table(name = "MemberCoupon")
@Getter
@Setter
@NoArgsConstructor
public class MemberCoupon {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberCouponId;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Members member;

    @ManyToOne
    @JoinColumn(name = "couponId", nullable = false)
    private Coupon coupon;
}

