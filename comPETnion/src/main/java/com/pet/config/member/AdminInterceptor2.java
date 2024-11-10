package com.pet.config.member;

import org.springframework.web.servlet.HandlerInterceptor;

import com.pet.model.admin.Admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminInterceptor2 implements HandlerInterceptor {

//	public boolean preHandle(HttpServletRequest request,HttpServletResponse response , Object handler)throws Exception{
//		String requestUri = request.getRequestURI();
//	
//		 HttpSession session = request.getSession(false);
//		 if(session != null && session.getAttribute("admin") != null) {
//			 return true;
//		 }
//		 response.sendRedirect("/comPETnion/public/login");
//		 return false;
//	
//	}
}
		 	 		 

