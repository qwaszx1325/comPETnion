package com.pet.dto.order;

import java.time.LocalDate;
import java.util.Date;

public class OrderDetailDto {

	private String orderId;
    private String productId;
    private Integer quantity;
    private Integer cost;
    private String productImageMain;
    private String productTitle;
    private Integer memberId;
    private String memberName;
    private Integer shippingCost;
    private LocalDate createDate;
    private String orderStatusId;
    private Boolean isReviewed;
    private String productDesc;
    private Integer productPrice;
    private Date productLaunchDate;
    
    private Integer orderDetailId;
    
    
    
	public Integer getOrderDetailId() {
		return orderDetailId;
	}
	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
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
	public String getProductImageMain() {
		return productImageMain;
	}
	public void setProductImageMain(String productImageMain) {
		this.productImageMain = productImageMain;
	}
	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public Integer getShippingCost() {
		return shippingCost;
	}
	public void setShippingCost(Integer shippingCost) {
		this.shippingCost = shippingCost;
	}
	public LocalDate getCreateDate() {
		return createDate;
	}
	public void setCreateDate(LocalDate createDate) {
		this.createDate = createDate;
	}
	public String getOrderStatusId() {
		return orderStatusId;
	}
	public void setOrderStatusId(String orderStatusId) {
		this.orderStatusId = orderStatusId;
	}
	public Boolean getIsReviewed() {
		return isReviewed;
	}
	public void setIsReviewed(Boolean isReviewed) {
		this.isReviewed = isReviewed;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public Integer getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(Integer productPrice) {
		this.productPrice = productPrice;
	}
	public Date getProductLaunchDate() {
		return productLaunchDate;
	}
	public void setProductLaunchDate(Date productLaunchDate) {
		this.productLaunchDate = productLaunchDate;
	}

	public OrderDetailDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public OrderDetailDto(String orderId, String productId, Integer quantity, Integer cost, String productImageMain,
			String productTitle, Integer memberId, String memberName, Integer shippingCost, LocalDate createDate,
			String orderStatusId, Boolean isReviewed, String productDesc, Integer productPrice, Date productLaunchDate
			) {
		super();
		this.orderId = orderId;
		this.productId = productId;
		this.quantity = quantity;
		this.cost = cost;
		this.productImageMain = productImageMain;
		this.productTitle = productTitle;
		this.memberId = memberId;
		this.memberName = memberName;
		this.shippingCost = shippingCost;
		this.createDate = createDate;
		this.orderStatusId = orderStatusId;
		this.isReviewed = isReviewed;
		this.productDesc = productDesc;
		this.productPrice = productPrice;
		this.productLaunchDate = productLaunchDate;
	}
    
    
    
    
    
    
    
    
}
