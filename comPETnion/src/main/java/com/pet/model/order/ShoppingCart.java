package com.pet.model.order;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "ShoppingCart")
public class ShoppingCart {
	
	@Id @Column(name = "PRODUCTID")
	private Integer productId;
	@Column(name = "MEMBERID")
	private Integer memberId;
	@Column(name = "QUANTITY")
	private Integer quantity;
	
	
	
	
	public ShoppingCart(Integer productId, Integer memberId, Integer quantity) {
		super();
		this.productId = productId;
		this.memberId = memberId;
		this.quantity = quantity;
	}
	public ShoppingCart() {
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	
}
