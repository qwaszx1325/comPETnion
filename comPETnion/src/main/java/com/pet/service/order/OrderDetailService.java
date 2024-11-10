package com.pet.service.order;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dto.order.OrderDetailDto;
import com.pet.model.order.OrderDetailView;
import com.pet.model.order.Orders;
import com.pet.repository.order.OrderDetailRepository;
import com.pet.repository.order.OrderDetailViewRepository;
import com.pet.repository.order.OrderRepository;

@Service
public class OrderDetailService {
	@Autowired
	OrderDetailViewRepository orderDetailViewRepository;
	@Autowired
	OrderRepository orderRepository;
	
	
	public List<OrderDetailView> findOrderDetail(String orderId) {
		
		
		return orderDetailViewRepository.findByOrderId(orderId);
	}
	public Orders findRecipientInfoByOrderId(String orderId) {
		 Optional<Orders> byId = orderRepository.findById(orderId);
		 return byId.get();
	}
	//接收Dto資料 用來評價用
	 public List<OrderDetailDto> getCompletedOrderDetails(Integer memberId) {
	        List<Object[]> results = orderDetailViewRepository.findCompletedOrderDetailsWithProductInfo(memberId);
	        
	        return results.stream().map(this::mapToOrderDetailDto).collect(Collectors.toList());
	    }

	 private OrderDetailDto mapToOrderDetailDto(Object[] row) {
		    OrderDetailDto dto = new OrderDetailDto();

		    dto.setOrderId(row[0] != null ? row[0].toString() : null);
		    dto.setProductId(row[1] != null ? row[1].toString() : null);
		    dto.setQuantity(row[2] != null ? Integer.parseInt(row[2].toString()) : null);
		    dto.setCost(row[3] != null ? Integer.parseInt(row[3].toString()) : null);
		    dto.setProductImageMain(row[4] != null ? row[4].toString() : null);
		    dto.setProductTitle(row[5] != null ? row[5].toString() : null);
		    dto.setMemberId(row[6] != null ? Integer.parseInt(row[6].toString()) : null);
		    dto.setMemberName(row[7] != null ? row[7].toString() : null);
		    dto.setShippingCost(row[8] != null ? Integer.parseInt(row[8].toString()) : null);
		    dto.setCreateDate(row[9] != null ? ((java.sql.Date) row[9]).toLocalDate() : null);
		    dto.setOrderStatusId(row[10] != null ? row[10].toString() : null);
		    dto.setIsReviewed(row[11] != null ? (Boolean) row[11] : null);
		    dto.setOrderDetailId(row[12] != null ? Integer.parseInt(row[12].toString()) : null);
		    dto.setProductDesc(row[13] != null ? row[13].toString() : null);
		    dto.setProductPrice(row[14] != null ? Integer.parseInt(row[14].toString()) : null);
		    dto.setProductLaunchDate(row[15] != null ? (java.util.Date) row[15] : null);

		    return dto;
		}
	
	 public List<Orders> findCompletedOrdersByMemberId(Integer memberId) {
	        return orderRepository.findCompletedOrdersByMemberId(memberId, 3); // Assuming '3' is the status for completed orders
	    }
	 public List<OrderDetailView> getCompletedOrders(Integer memberId) {
	        return orderDetailViewRepository.findCompletedOrdersByMemberId(memberId);
	    }
}
