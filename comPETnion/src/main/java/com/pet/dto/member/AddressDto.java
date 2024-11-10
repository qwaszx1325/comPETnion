package com.pet.dto.member;

public class AddressDto {
	
	private String memberAddress;
    private int count;
    
   
	public AddressDto(String memberAddress, int count) {
		super();
		this.memberAddress = memberAddress;
		this.count = count;
	}
	
	public String getMemberAddress() {
		return memberAddress;
	}
	public int getCount() {
		return count;
	}
    
    
}
