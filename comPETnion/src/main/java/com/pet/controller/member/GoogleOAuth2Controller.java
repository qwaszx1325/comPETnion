package com.pet.controller.member;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pet.config.member.GoogleOAuth2Config;
import com.pet.model.member.Members;
import com.pet.service.member.MembersService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class GoogleOAuth2Controller {

	@Autowired
	private GoogleOAuth2Config googleOAuth2Config;

	@Autowired
	private MembersService membersService;

	private final String scope = "https://www.googleapis.com/auth/userinfo.email";

	@GetMapping("/public/google-login")
	public String googleLogin(HttpServletResponse response) {
		String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?" + "client_id="
				+ googleOAuth2Config.getClientId() + "&response_type=code" + "&scope=openid%20email%20profile"
				+ "&redirect_uri=" + googleOAuth2Config.getRedirectUri() + "&state=state";

		return "redirect:" + authUrl;
	}

	@GetMapping("/google-callback")
	public String googleCallback(@RequestParam(required = false) String code, Model model, HttpSession httpSession,RedirectAttributes redirectAttributes) {

		if (code == null) {
			String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?" + "client_id="
					+ googleOAuth2Config.getClientId() + "&redirect_uri=" + googleOAuth2Config.getRedirectUri()
					+ "&scope=" + scope;
			return "redirect:" + authUrl;
		} else {
			RestClient restClient = RestClient.create();

			try {
				// 準備 requestbody // 拿 code 換 token
				String requestBody = UriComponentsBuilder.newInstance().queryParam("code", code)
						.queryParam("client_id", googleOAuth2Config.getClientId())
						.queryParam("client_secret", googleOAuth2Config.getClientSecret())
						.queryParam("redirect_uri", googleOAuth2Config.getRedirectUri())
						.queryParam("grant_type", "authorization_code").build().getQuery();

				String credentials = restClient.post().uri("https://oauth2.googleapis.com/token")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED).body(requestBody).retrieve()
						.body(String.class);

				System.out.println("credentials" + credentials);

				// json 字串傳換為 JsonNode (Java 物件)
				JsonNode jsonNode = new ObjectMapper().readTree(credentials);
				String accessToken = jsonNode.get("access_token").asText();
				String idToken = jsonNode.get("id_token").asText();

				// 拿到 token 後再去 google 請求一次，找 user
				String payloadResponse = restClient.get()
						.uri("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + accessToken)
						.header("Authorization", "Bearer " + idToken).retrieve().body(String.class);

				System.out.println("payloadResponse" + payloadResponse);

				JsonNode userinfo = new ObjectMapper().readTree(payloadResponse);
				String loginUserEmail = userinfo.get("email").asText();
				String loginUserName = userinfo.get("name").asText();
				String loginUserImg = userinfo.get("picture").asText();
				byte[] imageBytes = getImageBytes(loginUserImg);
			
				boolean result = membersService.checkMemberEmailExist(loginUserEmail);
				
			
					if(result) {
						Members member = membersService.selectMemberByEmail(loginUserEmail);
						
						//檢查會員狀態
						switch(member.getMemberstatusId()) {
						
						case 1://正常狀態
					
							member.setMemberstatusId(1);//確保狀態正常
							httpSession.setAttribute("member",member);
							model.addAttribute("alertLogin", "success");
							model.addAttribute("member", member);
							membersService.saveMembers(member);
							return "member/getMember";
//							return "redirect:/";
						
						case 2://封鎖狀態
							redirectAttributes.addFlashAttribute("alertLogin", "blocked");
				            redirectAttributes.addFlashAttribute("errorMessage", "您的帳戶已被封鎖，無法登入。如有疑問，請聯繫客服。");
			                return "redirect:/public/login";
						
						}
					}else {
						//法2 新用戶註冊邏輯
						//把google登入帳戶的值塞進去
						Members members = new Members();
						members.setMemberEmail(loginUserEmail);
						members.setMemberName(loginUserName);
					    members.setMemberImg(imageBytes);
						
						members.setMemberstatusId(1);
						Members member = membersService.registerMemberByGoogle(members);
						model.addAttribute("alertLogin", "success");
						model.addAttribute("member", member);
						httpSession.setAttribute("member",member);
						 return "member/getMember";
//						 return "redirect:/";
					}
					}catch (Exception e) {
					e.printStackTrace();
				    return "redirect:/public/login";
				}	
				}
		redirectAttributes.addFlashAttribute("alertLogin", "failure");
	    redirectAttributes.addFlashAttribute("errorMessage", "帳號或密碼錯誤");
		return "redirect:/";
		}

//				if (result) {
//					
//					Members member = membersService.selectMemberByEmail(loginUserEmail);
//					member.setMemberstatusId(1);
//					httpSession.setAttribute("member",member);
//					model.addAttribute("alertLogin", "success");
//					model.addAttribute("member", member);
//					membersService.saveMembers(member);
//					
//				} else {
//					//法1 在service 存資料到bean的做法
////					Members member = membersService.registerMemberByGoogle(loginUserEmail,loginUserName);
////					httpSession.setAttribute("member",member);
////					member.setMemberstatusId(1);
//					
//					//法2
//					Members members = new Members();
//					members.setMemberEmail(loginUserEmail);
//					members.setMemberName(loginUserName);
//				    members.setMemberImg(imageBytes);
//					
//					members.setMemberstatusId(1);
//					Members member = membersService.registerMemberByGoogle(members);
//					model.addAttribute("alertLogin", "success");
//					model.addAttribute("member", member);
//					httpSession.setAttribute("member",member);
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				return "member/memberLogin";
//			}
//			return "redirect:/";
//		}

	public byte[] getImageBytes(String imageUrl) throws Exception {
		URL url = new URL(imageUrl);
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		try (InputStream inputStream = url.openStream()) {
			int n = 0;
			byte[] buffer = new byte[1024];

			// 循環讀取數據,直到到達流的末尾(-1)
			while (-1 != (n = inputStream.read(buffer))) {
				output.write(buffer, 0, n);
			}
		}

		return output.toByteArray();
	}
}