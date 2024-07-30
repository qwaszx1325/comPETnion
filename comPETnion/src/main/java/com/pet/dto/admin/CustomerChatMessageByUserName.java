package com.pet.dto.admin;

public class CustomerChatMessageByUserName {

	private String userId;
	private String userName;
	
	
	
	public CustomerChatMessageByUserName() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CustomerChatMessageByUserName(String userId, String userName) {
		super();
		this.userId = userId;
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}
