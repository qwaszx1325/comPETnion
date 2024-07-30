package com.pet.config.member;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

	 @Bean
	    public AuthenticationInterceptor authenticationInterceptor() {
	        return new AuthenticationInterceptor();
	    }
	 @Bean
	 public AdminInterceptor AdminInterceptor() {
		 return new AdminInterceptor();
	 }
	
	 @Override
	    public void addInterceptors(InterceptorRegistry registry) {
	        registry.addInterceptor(authenticationInterceptor())
	        //攔截器將應用於所有以 "/member/" 開頭的 URL
	                .addPathPatterns("/member/**");
	         // URL 模式，表示哪些路徑應該被排除在攔截器之外
	        
	        registry.addInterceptor(AdminInterceptor())
	        //攔截器將應用於所有以 "/admin/" 開頭的 URL
	                .addPathPatterns("/admin/**");
	    }
}
//這裡的所有路徑都是 URL 路徑，不是視圖名稱。