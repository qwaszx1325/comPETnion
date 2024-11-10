package com.pet.repository.order;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.order.ShoppingCartView;

public interface ShoppingCartViewRepository extends JpaRepository<ShoppingCartView, String>{

	@Query("from ShoppingCartView where memberId=:memberId")
	public List<ShoppingCartView> findMemberShoppingCartById(@Param("memberId") Integer memberId);
}
