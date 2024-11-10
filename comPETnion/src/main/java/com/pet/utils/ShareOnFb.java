package com.pet.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.FacebookType;
import com.restfb.Parameter;

import com.pet.model.adopt.Adoptions;

@Service
public class ShareOnFb {

	@Value("${facebook.page.access-token}")
	private String pageAccessToken;

	@Value("${facebook.page.id}")
	private String pageId;

	@Value("${your.website.url}")
	private String websiteUrl; // ngrok網址

	@Async
	public void shareCase(Adoptions adoptions) {
		FacebookClient facebookClient = new DefaultFacebookClient(pageAccessToken, Version.LATEST);

		String message = String.format("來看看最新的寵物領養案件：%s\n！%s", adoptions.getPetName(), adoptions.getPetDescription());

		// 構建連結到特定寵物案件的URL，使用查詢參數
		String link = websiteUrl + "/public/adopt/caseDetails?petCaseId=" + adoptions.getPetCaseId();

		try {
			FacebookType response = facebookClient.publish(pageId + "/feed", FacebookType.class,
					Parameter.with("message", message), 
					Parameter.with("link", link));
			System.out.println("FB post 成功發布，id: " + response.getId());
		} catch (Exception e) {
			System.err.println("Facebook 錯誤: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
