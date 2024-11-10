package com.pet.service.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dto.member.AddressDto;
import com.pet.dto.member.AgeDto;
import com.pet.model.member.Members;
import com.pet.repository.member.MembersRepository;

@Service
public class MemberChartService {

	@Autowired
	private MembersRepository membersRepo;

	public List<AddressDto> getAddress() {
		List<Members> allMembers = membersRepo.findAll();
		// 创建一个Map来存储每个地址的计数
		Map<String, Integer> addressCountMap = new HashMap<>();

		for (Members member : allMembers) {
			String address = member.getMemberAddress();
			// 如果地址已经在Map中,增加计数;否则,添加到Map中并设置计数为1
			if(address!=null&&!address.trim().isEmpty()) {	
				addressCountMap.put(address, addressCountMap.getOrDefault(address, 0) + 1);
			}
		}

		// 创建结果列表
		List<AddressDto> result = new ArrayList<>();
		// 将Map中的数据转换为AddresstDto对象列表
		for (Map.Entry<String, Integer> entry : addressCountMap.entrySet()) {
			result.add(new AddressDto(entry.getKey(), entry.getValue()));
		}
		return result;
	}

	public List<AgeDto> getAgeDistribution() {
		// 步驟 1: 創建一個 Map 來存儲年齡分佈

		List<Members> members = membersRepo.findAll(); // 從數據庫獲取所有會員
		Map<String, Integer> ageDistribution = new HashMap<>();

		for (Members member : members) {
			String ageGroup = getAgeGroup(member.getMemberAge());

			if(!ageGroup.equals("未知")) {
				// 使用 getOrDefault 方法來簡化代碼
				ageDistribution.put(ageGroup, ageDistribution.getOrDefault(ageGroup, 0) + 1);
			}		
		}

		List<AgeDto> result = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : ageDistribution.entrySet()) {
			result.add(new AgeDto(entry.getKey(), entry.getValue()));
		}

		return result;
	}

	private String getAgeGroup(String ageString) {
		if(ageString == null || ageString.trim().isEmpty()) {
			return "未知";
		}
		try {
			
		int age = Integer.parseInt(ageString);
		if (age < 18)
			return "18歲以下";
		else if (age < 30)
			return "18-29歲";
		else if (age < 40)
			return "30-39歲";
		else if (age < 50)
			return "40-49歲";
		else if (age < 60)
			return "50-59歲";
		else
			return "60歲以上";
		}catch(NumberFormatException e) {
			return "未知";
		}
	}

}
