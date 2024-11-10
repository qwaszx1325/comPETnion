package com.pet.aop.member;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;

@Aspect
@Component
public class LoginLoggingAspect {

	private static final Logger LOGGER = LogManager.getLogger(LoginLoggingAspect.class);
	
	
	@AfterReturning(
			//						指針對這個方法存log(AOP只偵測到這個package)
			pointcut = "execution(* com.pet.controller.member.MembersController.loginMember(..))",
			returning = "result"
			)
	public void afterLoginAttempt(JoinPoint joinPoint, Object result) {
		Object[] args = joinPoint.getArgs();
		String email = (String) args[0];
		HttpSession session = null;
		for (Object arg : args) {
            if (arg instanceof HttpSession) {
                session = (HttpSession) arg;
                break;
            }
        }

        if (session != null) {
            if (session.getAttribute("admin") != null) {
            	LOGGER.info("Admin logged in: {}", email);
            } else if (session.getAttribute("member") != null) {
            	LOGGER.info("Member logged in: {}", email);
            } else {
            	LOGGER.info("Failed login attempt for: {}", email);
            }
        } else {
        	LOGGER.warn("Session not found in method arguments");
        }
		
	}
}
