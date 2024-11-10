package com.pet.controller.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pet.dto.member.AddressDto;
import com.pet.dto.member.AgeDto;
import com.pet.service.member.MemberChartService;
import com.pet.service.member.MembersService;
import com.fasterxml.jackson.databind.ObjectMapper;
@Controller
public class ChartController {
	
	@Autowired
	private MemberChartService memberChartService;

	
	 @GetMapping("/admin/chart")
	    public String getChart(Model model) {
	        List<AddressDto> addresses = memberChartService.getAddress();
	        List<AgeDto> ageDistribution = memberChartService.getAgeDistribution();
	        // 將地址和數量分別轉換為 JSON 字串
	        ObjectMapper mapper = new ObjectMapper();
	        try {
	            // 處理地址數據
	            List<String> validAddresses = addresses.stream()
	                .map(AddressDto::getMemberAddress)
	                .filter(Objects::nonNull)
	                .collect(Collectors.toList());
	            
	            List<Integer> validCounts = addresses.stream()
	                .map(AddressDto::getCount)
	                .filter(Objects::nonNull)
	                .collect(Collectors.toList());
	            
	            // 處理年齡數據
	            List<String> validAgeGroups = ageDistribution.stream()
	                .map(AgeDto::getAgeGroup)
	                .filter(Objects::nonNull)
	                .collect(Collectors.toList());
	            
	            List<Integer> validAgeCounts = ageDistribution.stream()
	                .map(AgeDto::getCount)
	                .filter(Objects::nonNull)
	                .collect(Collectors.toList());
	            
	            model.addAttribute("addresses", mapper.writeValueAsString(validAddresses));
	            model.addAttribute("counts", mapper.writeValueAsString(validCounts));
	            model.addAttribute("ageGroups", mapper.writeValueAsString(validAgeGroups));
	            model.addAttribute("ageCounts", mapper.writeValueAsString(validAgeCounts));
	        } catch (JsonProcessingException e) {
	            e.printStackTrace();
	            // 可以添加錯誤處理邏輯，例如添加錯誤信息到模型中
	            model.addAttribute("error", "處理數據時發生錯誤");
	        }
	        
	        return "admin/chart";
	    }
	 

}
