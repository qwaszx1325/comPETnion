package com.pet.member.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RecaptchaConfig {
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();// 創建並返回一個RestTemplate對象，用於發送HTTP請求
	}
	
	 @Value("${google.recaptcha.key.site}")
	    private String siteKey;
	    
	    @Value("${google.recaptcha.key.secret}")
	    private String secretKey;

		public String getSiteKey() {
			return siteKey;
		}

		public String getSecretKey() {
			return secretKey;
		}

	    
	    
}
