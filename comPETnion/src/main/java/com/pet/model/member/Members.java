package com.pet.model.member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pet.model.product.MemberCoupon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@Setter
@Getter
@Entity @Table(name = "members")
@Component
public class Members {

	@Id @Column(name="memberId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memberId;
	
	@Column(name = "memberName")
	private String memberName;
	
	@Column(name = "memberPassword", nullable = true)
	private String memberPassword;
	
	private Boolean isGoogleLogin;
	
	@Column(name="memberEmail",unique = true, nullable = false)
	private String memberEmail;
	
	@Column(name="memberAge")
	private String memberAge;
	
	@Column(name="memberPhone")
	private String memberPhone;
	
	@Column(name="memberAddress")
	private String memberAddress;
	
	@Lob
	@Column(name="memberImg")
	private byte[] memberImg;
	
	private Integer memberstatusId;
	
	
	private boolean isVerified;  // 郵箱是否已驗證

    private String verificationToken;  // 郵箱驗證令牌

    private LocalDateTime tokenExpiryDate;  // 令牌過期時間

    private String resetPasswordToken;

    private LocalDateTime resetPasswordTokenExpiry;
    
    @OneToMany(mappedBy = "member")
    private List<MemberCoupon> memberCoupons;
  
}
