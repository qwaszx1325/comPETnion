package com.pet.config.member;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
	
	@Bean
	//springboot3之後public可以不用寫
	//passwordEncoder一定要是PasswordEncoder小寫開頭
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
