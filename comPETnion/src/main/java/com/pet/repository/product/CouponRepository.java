package com.pet.repository.product;


import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.product.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    Coupon findByCode(String code);
}