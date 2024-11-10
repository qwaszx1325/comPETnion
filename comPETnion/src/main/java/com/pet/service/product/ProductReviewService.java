package com.pet.service.product;

import com.pet.dto.order.RatingDto;
import com.pet.model.order.OrderDetail;
import com.pet.model.order.OrderDetailView;
import com.pet.model.order.Orders;
import com.pet.model.product.ProductReview;
import com.pet.repository.order.OrderDetailRepository;
import com.pet.repository.order.OrderDetailViewRepository;
import com.pet.repository.order.OrderRepository;
import com.pet.repository.product.ProductReviewRepository;
import com.pet.service.order.OrderDetailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductReviewService {
    @Autowired
    private ProductReviewRepository reviewRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
    
    @Autowired
    private OrderDetailViewRepository orderDetailViewRepository;
    

    public List<OrderDetailView> getCompletedOrders(Integer memberId) {
        return orderDetailViewRepository.findCompletedOrdersByMemberId(memberId);
    }

//    public List<OrderDetailView> getCompletedOrders(Integer memberId) {
//        List<Orders> completedOrders = orderDetailService.findCompletedOrdersByMemberId(memberId);
//        // Assuming that OrderDetailView is used to show order details
//        return completedOrders.stream()
//                .flatMap(order -> orderDetailService.findOrderDetail(order.getOrderId()).stream())
//                .collect(Collectors.toList());
//    }

    //儲存商品評價
    public void saveReview(RatingDto ratingDto,Integer memberId,String memberName) {
    	Integer orderDetailId = ratingDto.getOrderDetailId();
        ProductReview review = new ProductReview();
        //保存評價
        review.setComment(ratingDto.getComment());
        review.setMemberId(memberId);
        review.setMemberName(memberName);
        review.setProductId(ratingDto.getProductId());
        review.setOrderDetailId(orderDetailId);
        review.setRating(ratingDto.getRating());
        
        reviewRepository.save(review);
        //訂單明細改成以評價過
        orderDetailRepository.updateIsReviewed(orderDetailId);
    }
    
    //搜尋商品所有評價
    public List<ProductReview> findReviewByProductId(Integer productId){
    	List<ProductReview> reviewList = reviewRepository.findByProductId(productId);
    	return reviewList;
    }
    
    
}