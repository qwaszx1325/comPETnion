package com.pet.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pet.model.order.OrderDetail;
import com.pet.model.order.OrderDetailView;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer>{

	@Query("From OrderDetailView WHERE orderId=:orderId")
	public List<OrderDetailView> findByOrderId(@Param("orderId") String orderId);
	
	@Modifying
	@Transactional
	@Query("UPDATE OrderDetail SET isReviewed = true WHERE orderDetailId = :orderDetailId")
	public void updateIsReviewed(@Param("orderDetailId") Integer orderDetailId);

		
}
