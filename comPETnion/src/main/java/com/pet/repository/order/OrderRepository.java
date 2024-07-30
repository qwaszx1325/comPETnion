package com.pet.repository.order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pet.model.order.MonthlyOrderTotalsWithMembers;
import com.pet.model.order.OrderView;
import com.pet.model.order.Orders;

public interface OrderRepository extends JpaRepository<Orders, String> {

	@Modifying
	@Transactional
	@Query("UPDATE Orders SET orderStatusId =:status WHERE orderId = :orderId")
	public void updateOrderStatus(@Param("orderId") String orderId, @Param("status") Integer status);

	@Query("FROM OrderView WHERE memberId = :memberId AND orderStatusId=:orderStatusId")
	public List<OrderView> findAllOrderByStatus(@Param("memberId") Integer memberId,
			@Param("orderStatusId") Integer orderStatusId);

	@Query("FROM OrderView WHERE memberId = :memberId")
	public List<OrderView> findAllOrderByMemberId(@Param("memberId") Integer memberId);

	@Query("FROM OrderView WHERE memberId = :memberId AND createDate >= :startDate AND createDate < :endDate AND (orderStatusId = '2' OR orderStatusId = '3')")
	public List<OrderView> findAllOrderByDateOneMonth(@Param("memberId") Integer memberId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);
	
	@Query("FROM OrderView WHERE memberId = :memberId AND createDate >= :startDate AND createDate < :endDate AND orderStatusId IN :orderStatusId ORDER BY createDate DESC" )
	public List<OrderView> findAllOrderByDate(@Param("memberId") Integer memberId,@Param("orderStatusId") List<Integer> orderStatusId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);
	
	@Query("SELECT o FROM Orders o LEFT JOIN FETCH o.orderDetailBean WHERE o.memberId = :memberId AND o.orderStatusId = 3")
    List<Orders> findCompletedOrdersByMemberId(@Param("memberId") Integer memberId);

	
	@Query("SELECT o FROM Orders o WHERE o.memberId = :memberId AND o.orderStatusId = :status")
    List<Orders> findCompletedOrdersByMemberId(@Param("memberId") Integer memberId, @Param("status") Integer status);

//	@Query("SELECT o FROM Orders o WHERE o.memberId = :memberId AND o.orderStatusId = 3")
//    List<Orders> findCompletedOrdersByMemberId(@Param("memberId") Integer memberId);
	
}
