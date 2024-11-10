package com.pet.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.dto.order.OrderDetailDto;
import com.pet.model.order.OrderDetailView;

public interface OrderDetailViewRepository extends JpaRepository<OrderDetailView, String>{
	 @Query("FROM OrderDetailView WHERE memberId = :memberId AND orderStatusId = '3'")
	    List<OrderDetailView> findCompletedOrdersByMemberId(@Param("memberId") Integer memberId);

	 List<OrderDetailView> findByOrderId(String orderId);
	 
	 @Query(value = "SELECT odv.orderId, odv.productId, odv.quantity, odv.cost, " +
		       "odv.productImageMain, odv.productTitle, odv.memberId, odv.memberName, odv.shippingCost, " +
		       "odv.createDate, odv.orderStatusId, odv.isReviewed,odv.orderDetailId ,p.productDesc, p.productPrice, " +
		       "p.productLaunchDate " +
		       "FROM orderdetailview odv " +
		       "JOIN Product p ON CAST(odv.productId AS int) = p.productID " +
		       "WHERE odv.memberId = :memberId AND odv.orderStatusId = '3'",
		       nativeQuery = true)
		List<Object[]> findCompletedOrderDetailsWithProductInfo(@Param("memberId") Integer memberId);
	
}
