package com.pet.model.order;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name="MonthlyOrderTotalsWithMembers")
public class MonthlyOrderTotalsWithMembers {

	
	private Integer memberId;
	private String year;
	@Id
	private String month;
	private String totalCost;
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(String totalCost) {
		totalCost = totalCost;
	}
	
	
}
