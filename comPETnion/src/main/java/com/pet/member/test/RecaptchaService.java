package com.pet.member.test;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {
	private static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";

	@Autowired
    private RestTemplate restTemplate; // 用於發送 HTTP 請求
	
	
	 @Autowired
	  private RecaptchaConfig recaptchaConfig; // 注入配置
	 
	 
	 public boolean verifyRecaptcha(String response) {
	        // 構建驗證 URL
	        String url = RECAPTCHA_URL + "?secret=" + recaptchaConfig.getSecretKey() + "&response=" + response;
	        
	        // 發送 POST 請求到 Google 的驗證 API
	        RecaptchaResponse recaptchaResponse = restTemplate.postForObject(url, Collections.emptyList(), RecaptchaResponse.class);
	        
	        // 返回驗證結果
	        return recaptchaResponse.isSuccess();
	    }
}
