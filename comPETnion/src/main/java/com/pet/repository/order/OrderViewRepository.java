package com.pet.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.order.OrderView;
import com.pet.model.order.Orders;

public interface OrderViewRepository extends JpaRepository<OrderView, String>{

	

	
}
