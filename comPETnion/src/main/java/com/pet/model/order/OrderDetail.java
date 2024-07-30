package com.pet.model.order;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orderDetail")
public class OrderDetail {
	@Id
	@Column(name = "ORDERDETAILID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderDetailId;
	@Column(name = "productId")
	private Integer productId;
	@Column(name = "quantity")
	private Integer quantity;
	@Column(name = "cost")
	private Integer cost;
	private Boolean isReviewed;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDERID", nullable = false)
	private Orders orders;

	// 使用sql的view會用到
//	private String img;
//	private String title;
	
	

	public Orders getOrderBean() {
		return orders;
	}


	public Boolean getIsReviewed() {
		return isReviewed;
	}


	public void setIsReviewed(Boolean isReviewed) {
		this.isReviewed = isReviewed;
	}


	public Orders getOrders() {
		return orders;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}

	public void setOrderBean(Orders orderBean) {
		this.orders = orderBean;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getCost() {
		return cost;
	}

	public void setCost(Integer cost) {
		this.cost = cost;
	}

}
