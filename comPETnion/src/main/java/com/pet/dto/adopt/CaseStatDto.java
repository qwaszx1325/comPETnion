package com.pet.dto.adopt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CaseStatDto {
	
	private String month;
    private long totalCases;
    private long published;
    private long adopted;
    
}
