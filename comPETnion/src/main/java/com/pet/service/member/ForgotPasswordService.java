package com.pet.service.member;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pet.model.member.Members;
import com.pet.repository.member.MembersRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ForgotPasswordService {

	@Autowired
	private MembersRepository membersRepo;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// 處理忘記密碼請求
	public boolean processForgotPassword(String memberEmail) throws MessagingException {

		Members member = membersRepo.findByMemberEmail(memberEmail);
		if (member == null) {

			return false;
		}

		// 生成重置令牌
		String token = generateResetToken();
		// 設置會員的重置令牌
		member.setResetPasswordToken(token);
		// 設置令牌過期時間為30分鐘後
		member.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(30));

		// 保存更新後的會員信息到資料庫
		membersRepo.save(member);

		// 發送重置密碼郵件
		sendResetPasswordEmail(member, token);
		return true;
	}

	// 生成重置令牌
	private String generateResetToken() {
		return UUID.randomUUID().toString();
	}

	// 發送重置密碼郵件
	@Async
	private void sendResetPasswordEmail(Members member, String token) throws MessagingException {

//		String url = "http://localhost:8081/comPETnion";
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		String htmlMsg = String.format(
				"""
						    <!DOCTYPE html>
						    <html lang="zh-TW">
						    <head>
						        <meta charset="UTF-8">
						        <meta name="viewport" content="width=device-width, initial-scale=1.0">
						        <title>密碼重置</title>
						        <style>
						            body {
						                font-family: Arial, sans-serif;
						                background-color: #f4f4f4;
						                margin: 0;
						                padding: 0;
						            }
						            .container {
						                width: 100%%;
						                max-width: 600px;
						                margin: 0 auto;
						                background-color: #ffffff;
						                padding: 20px;
						                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
						            }
						            .header {
						                text-align: center;
						                padding: 20px 0;
						            }
						            .header img {
						                max-width: 100px;
						                height: auto;
						            }
						            .content {
						                margin: 20px 0;
						            }
						            .content h1 {
						                color: #333333;
						            }
						            .content p {
						                line-height: 1.6;
						                color: #666666;
						            }
						            .footer {
						                text-align: center;
						                padding: 20px 0;
						                color: #999999;
						                font-size: 12px;
						            }
						            .button {
						                display: inline-block;
						                padding: 10px 20px;
						                margin-top: 20px;
						                background-color: #876d5a;
						                color: white !important;
						                text-decoration: none;
						                border-radius: 5px;
						            }
						        </style>
						    </head>
						    <body>
						        <div class="container">
						            <div class="header">
						                <img src="https://i.imgur.com/M34EE1U.png" alt="Company Logo">
						            </div>
						            <div class="content">
						                <h1>密碼重置</h1>
						                <p>親愛的用戶，</p>
						                <p>我們收到了您的密碼重置請求。請點擊下面的按鈕來重置您的密碼：</p>
						                <a href="http://localhost:8081/comPETnion/public/reset/password?token=%s" class="button">重置密碼</a>
						                <p>此連結將在30分鐘後過期。如果您沒有請求重置密碼，請忽略此郵件。</p>
						                <p>祝您使用愉快！</p>
						            </div>
						            <div class="footer">
						                <p>這是一封自動產生的郵件，請勿回覆。</p>
						                <p>© 2024 comPETnion. All rights reserved.</p>
						            </div>
						        </div>
						    </body>
						    </html>
						""",
				token);

		helper.setTo(member.getMemberEmail());
		helper.setSubject("密碼重置");
		helper.setText(htmlMsg, true);

		mailSender.send(mimeMessage);
	}



	// 驗證重置令牌
	public Members validateResetToken(String token) {

		Members member = membersRepo.findByResetPasswordToken(token);
		// 如果會員不存在或令牌已過期，返回null
		if (member == null || member.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
			return null;
		}

		return member;
	}

	// 重置密碼
	public void resetPassword(Members member, String newPassword) {
		// 使用密碼加密器加密新密碼
		member.setMemberPassword(passwordEncoder.encode(newPassword));
		// 清除重置令牌
		member.setResetPasswordToken(null);
		// 清除重置令牌過期時間
		member.setResetPasswordTokenExpiry(null);
		// 保存更新後的會員信息到資料庫
		membersRepo.save(member);
	}

}
