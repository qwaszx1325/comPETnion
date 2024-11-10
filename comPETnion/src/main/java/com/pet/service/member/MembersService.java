package com.pet.service.member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pet.config.redis.JedisConnectionFactory;
import com.pet.model.member.Members;
import com.pet.repository.member.MembersRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import redis.clients.jedis.Jedis;

@Service
public class MembersService {
	//用戶登入前贅詞
	private static final String CHECK_IN_KEY_PREFIX = "checkin:";
	
	@Autowired
	private MembersRepository membersRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender; // 注入郵件發送器

	
	//每日簽到
	public boolean dailyCheckIn(Integer memberId) {
		try (Jedis jedis = JedisConnectionFactory.getJedis()) {
            String key = CHECK_IN_KEY_PREFIX + memberId;
            long offset = getCurrentDayOffset();
            
            System.out.println(offset);
            // 檢查用戶是否已經簽到
            if (jedis.getbit(key, offset)) {
                return false; // 用戶已經簽到
            }
            
            // 設置簽到標記
            jedis.setbit(key, offset, true);
            return true;
        }
	}
	
	//判斷是否簽到
	public boolean hasCheckedIn(String memberId) {
        try (Jedis jedis = JedisConnectionFactory.getJedis()) {
            String key = CHECK_IN_KEY_PREFIX + memberId;
            long offset = getCurrentDayOffset();
            return jedis.getbit(key, offset);
        }
    }

	//計算連續簽到天數
    public Integer getConsecutiveCheckIns(Integer memberId) {
        try (Jedis jedis = JedisConnectionFactory.getJedis()) {
            String key = CHECK_IN_KEY_PREFIX + memberId;
            long offset = getCurrentDayOffset();
            Integer consecutive = 0;

            while (offset >= 0 && jedis.getbit(key, offset)) {
                consecutive++;
                offset--;
            }

            return consecutive;
        }
    }

    //取得當前日期天數
    private long getCurrentDayOffset() {
        return LocalDate.now(ZoneId.systemDefault()).toEpochDay();
    }
	
	
	
	public void deleteMember(Integer memberId) {
		membersRepo.deleteById(memberId);
	}

	public void updateMembersStatus(Integer memberstatusId, Integer memberId) {
		membersRepo.updateMembersStatus(memberstatusId, memberId);

	}

	public List<Members> findAllMember() {
		List<Members> members = membersRepo.findAll();
		System.out.println(members);
		return members;
	}

	public Members updateMember(Members updateMember) {

		return membersRepo.save(updateMember);
	}

	public Members findMembersById(Integer memberId) {
		Optional<Members> optional = membersRepo.findById(memberId);
		if (optional.isEmpty()) {
			return null;
		}
		return optional.get();
	}

	public Members saveMembers(Members members) {
		return membersRepo.save(members);
	}

	// 因為密碼加密 所以用會員姓名查詢(中介)
	public boolean checkMembersnameExist(String membername) {

		Members dbmember = membersRepo.findByMemberName(membername);

		if (dbmember == null) {
			return false;
		}
		return true;

	}

	public Members checkLogin(String memberEmail, String memberPassword) {

		Members dbMembers = membersRepo.findByMemberEmail(memberEmail);

		if (dbMembers == null) {
			return null;
		} else {
			String dbPassword = dbMembers.getMemberPassword();
			boolean result = passwordEncoder.matches(memberPassword, dbPassword);
			if (result) {
				return dbMembers;
			}

			return null;
		}

	}
	
	

	public boolean checkMemberEmailExist(String memberEmail) {

		Members dbMembers = membersRepo.findByMemberEmail(memberEmail);

		if (dbMembers == null) {
			return false;
		}
		return true;
	}

	public Members selectMemberByEmail(String memberEmail) {
		return membersRepo.findByMemberEmail(memberEmail);
	}

	public Members registerMember(Members registerMember) {

		return membersRepo.save(registerMember);
	}

	public Members findPhotosById(Integer memberId) {
		Optional<Members> optional = membersRepo.findById(memberId);

		if (optional.isPresent()) {
			return optional.get();
		}

		return null;
	}

	public Members findByMembername(String memberName) {
		return membersRepo.findByMemberName(memberName);
	}

	// 法1 在service 存資料到bean的做法
//	public Members registerMemberByGoogle(String memberEmail,String memberName) {
//		Members members = new Members();
//		members.setMemberEmail(memberEmail);
//		members.setMemberName(memberName);
//		members.setMemberstatusId(1);
//		members.setIsGoogleLogin(Boolean.TRUE);		
//		return membersRepo.save(members);
//	}

	// 法2 在controller存值的方法
	public Members registerMemberByGoogle(Members members) {
		members.setIsGoogleLogin(Boolean.TRUE);
		return membersRepo.save(members);
	}

	// 發送驗證郵件
	@Async
	public void sendVerificationEmail(String toEmail, String token) throws MessagingException {
//		String url = "http://localhost:8081/comPETnion";
		MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        String htmlMsg = String.format("""
                <!DOCTYPE html>
                <html lang="zh-TW">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>驗證您的電子信箱</title>
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
                            <h1>驗證您的電子信箱</h1>
                            <p>親愛的用戶，</p>
                            <p>感謝您註冊我們的服務。請點擊下面的按鈕來驗證您的電子信箱：</p>
                            <a href="http://localhost:8081/comPETnion/public/verify?token=%s" class="button">驗證電子信箱</a>
                            <p>此連結將在30分鐘後過期。如果您沒有註冊我們的服務，請忽略此郵件。</p>
                            <p>祝您使用愉快！</p>
                        </div>
                        <div class="footer">
                            <p>這是一封自動產生的郵件，請勿回覆。</p>
                            <p>© 2024 comPETnion. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
            """,token);

            helper.setTo(toEmail);
            helper.setSubject("驗證您的電子信箱");
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        
	}

	// 驗證郵箱
	public boolean verifyEmail(String token) {
		Members member = membersRepo.findByVerificationToken(token);
		if (member == null) {
			return false;
		}
		// 檢查令牌是否過期
		if (LocalDateTime.now().isAfter(member.getTokenExpiryDate())) {
			return false;
		}

		// 檢查是否已經驗證過
		if (member.isVerified()) {
			return true; // 已經驗證過，直接返回 true
		}

		// 更新會員狀態
		member.setVerified(true);
		member.setVerificationToken(null);
		member.setTokenExpiryDate(null);
		member.setMemberstatusId(1);// 設定會員狀態為已驗證
		membersRepo.save(member);
		return true;
	}
}
