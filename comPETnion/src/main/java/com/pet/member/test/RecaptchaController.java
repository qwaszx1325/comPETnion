package com.pet.member.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecaptchaController {

	@Autowired
    private RecaptchaService recaptchaService;
	
	 @PostMapping("/public/verify/recaptcha")
	    public ResponseEntity<Boolean> verifyRecaptcha(@RequestBody RecaptchaDto recaptchaDto) {
	        // 驗證 reCAPTCHA 並返回結果
	        boolean isValid = recaptchaService.verifyRecaptcha(recaptchaDto.getRecaptchaResponse());
	        return ResponseEntity.ok(isValid);
	    }
}
