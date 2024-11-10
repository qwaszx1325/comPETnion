package com.pet.controller.product;

import com.pet.dto.order.OrderDetailDto;
import com.pet.dto.order.RatingDto;
import com.pet.model.member.Members;
import com.pet.model.order.OrderDetail;
import com.pet.model.order.OrderDetailView;
import com.pet.model.order.Orders;
import com.pet.service.order.OrderDetailService;
import com.pet.service.order.OrderService;
import com.pet.service.product.ProductReviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProductReviewController {

    @Autowired
    private ProductReviewService reviewService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderDetailService orderDetailService;

    @SuppressWarnings("unchecked")
	@GetMapping("/member/reviews/completedOrders")
    public String showCompletedOrders(Model model, HttpSession session) {
        Members member = (Members) session.getAttribute("member");
        Integer memberId = member.getMemberId();
        List<OrderDetailDto> completedOrderDetailsWithProductInfo = orderDetailService.getCompletedOrderDetails(memberId); // 假設用戶 ID 為 1
        model.addAttribute("completedOrderDetailsWithProductInfo", completedOrderDetailsWithProductInfo);
        return "/product/finishedOrders";
    }
        
    
    @GetMapping("/add/{orderId}")
    public String addReview(@PathVariable String orderId, Model model) {
        model.addAttribute("orderId", orderId);
       
        return "/product/addProductReview";
    }

    @PostMapping("/member/reviews/add")
    public String saveReview(@ModelAttribute RatingDto ratingDto,HttpSession session ) {

        Members member = (Members) session.getAttribute("member");
        String memberName = member.getMemberName();
        Integer memberId = member.getMemberId();
        reviewService.saveReview(ratingDto,memberId,memberName);
        return "redirect:/member/reviews/completedOrders";
    }
}
