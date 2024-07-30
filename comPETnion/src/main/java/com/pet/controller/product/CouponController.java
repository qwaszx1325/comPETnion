package com.pet.controller.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.pet.model.product.Coupon;
import com.pet.service.product.CouponService;

import java.util.List;

@Controller
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    //添加優惠券
    @PostMapping("/admin/add")
    public ResponseEntity<Coupon> addCoupon(@RequestBody Coupon coupon) {
        Coupon savedCoupon = couponService.addCoupon(coupon);
        return new ResponseEntity<>(savedCoupon, HttpStatus.CREATED);
    }

    //獲取所有優惠券
    @GetMapping("/admin/all")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }
    
    //刪除優惠券
    @DeleteMapping("/admin/coupon/delete/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Integer id) {
        couponService.deleteCoupon(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PutMapping("/admin/update")
    public ResponseEntity<Coupon> updateCoupon(@RequestBody Coupon coupon) {
        Coupon updatedCoupon = couponService.updateCoupon(coupon);
        return ResponseEntity.ok(updatedCoupon);
    }

    //根據會員 ID 獲取該會員的所有優惠券
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Coupon>> getCouponsByMemberId(@PathVariable Integer memberId) {
        List<Coupon> coupons = couponService.getCouponsByMemberId(memberId);
        return ResponseEntity.ok(coupons);
    }

    //將優惠券分配給會員
    @PostMapping("/assign")
    public ResponseEntity<String> assignCouponToMember(@RequestParam Integer memberId, @RequestParam Integer couponId) {
        boolean assigned = couponService.assignCouponToMember(memberId, couponId);
        if (assigned) {
            return ResponseEntity.ok("Coupon assigned successfully");
        } else {
            return ResponseEntity.status(409).body("Coupon already assigned to the member");
        }
    }

    //從會員中移除優惠券
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCouponFromMember(@RequestParam Integer memberId, @RequestParam Integer couponId) {
        couponService.removeCouponFromMember(memberId, couponId);
        return ResponseEntity.ok("Coupon removed successfully");
    }

    //根據優惠券代碼獲取優惠券
    @GetMapping("/member/code/{code}")
    public ResponseEntity<Coupon> getCouponByCode(@PathVariable String code) {
        Coupon coupon = couponService.getCouponByCode(code);
        return ResponseEntity.ok(coupon);
    }
    
    @GetMapping("/coupons")
    @ResponseBody
    public List<Coupon> getAllCouponsOrder() {
        return couponService.getAllCoupons();
    }
}
