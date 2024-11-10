package com.pet.member.test;

public class RecaptchaResponse {
	
	 private boolean success; // 表示驗證是否成功
	 private String[] errorCodes; // 存儲錯誤代碼（如果有）
	 
	 
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String[] getErrorCodes() {
		return errorCodes;
	}
	public void setErrorCodes(String[] errorCodes) {
		this.errorCodes = errorCodes;
	} 

	 
	 
	 
}
