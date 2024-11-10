package com.pet.dto.member;

public class AgeDto {
	private String ageGroup;
    private int count;
	public String getAgeGroup() {
		return ageGroup;
	}
	public int getCount() {
		return count;
	}
	public AgeDto(String ageGroup, int count) {
		super();
		this.ageGroup = ageGroup;
		this.count = count;
	}
    
    
}
