package com.pet.model.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "ecpay_transactions")
@Entity
public class EcpayTransactions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String orderId;
	private String tradeNo;
	private String tradeAmt;
	private String paymentType;
	private String paymentDate;
	private String rtnCode;
	private String rtnMsg;
	private String merchantId;
	private String tradeDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getTradeAmt() {
		return tradeAmt;
	}
	public void setTradeAmt(String tradeAmt) {
		this.tradeAmt = tradeAmt;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getRtnCode() {
		return rtnCode;
	}
	public void setRtnCode(String rtnCode) {
		this.rtnCode = rtnCode;
	}
	public String getRtnMsg() {
		return rtnMsg;
	}
	public void setRtnMsg(String rtnMsg) {
		this.rtnMsg = rtnMsg;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}
	public EcpayTransactions() {
	}
	public EcpayTransactions(String orderId, String tradeNo, String tradeAmt, String paymentType,
			String paymentDate, String rtnCode, String rtnMsg, String merchantId, String tradeDate) {
		super();
		this.orderId = orderId;
		this.tradeNo = tradeNo;
		this.tradeAmt = tradeAmt;
		this.paymentType = paymentType;
		this.paymentDate = paymentDate;
		this.rtnCode = rtnCode;
		this.rtnMsg = rtnMsg;
		this.merchantId = merchantId;
		this.tradeDate = tradeDate;
	}
	
	
	
	
	
	
}
