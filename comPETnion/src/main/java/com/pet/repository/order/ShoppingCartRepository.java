package com.pet.repository.order;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pet.model.order.ShoppingCart;
import com.pet.model.order.ShoppingCartView;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Integer>{

	
	
	@Query("from ShoppingCart where memberId=:memberId and productId=:productId")
	public ShoppingCart checkShoopingCartByMemberId(@Param("memberId") Integer memberId,@Param("productId") Integer productId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM ShoppingCart WHERE memberId=:memberId AND productId=:productId")
	public void deleteAllShoopingCart(@Param("memberId") Integer memberId,@Param("productId") Integer productId);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM ShoppingCart WHERE productId=:productId AND memberId=:memberId")
	public void deleteOneProduct(@Param("productId") Integer productId,@Param("memberId") Integer memberId);
	


	
}
