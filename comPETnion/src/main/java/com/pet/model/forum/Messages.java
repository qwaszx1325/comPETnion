package com.pet.model.forum;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pet.model.member.Members;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "messages")
public class Messages {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer messagesId;

	@Column(length = Integer.MAX_VALUE)
	private String messageContent;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 若要在 thymeleaf 強制使用本格式，需雙層大括號
	@Temporal(TemporalType.TIMESTAMP)
	private Date messageDate;

//	@JsonIgnore // 不要 JSON 序列化本屬性，通常用在 Entity 的關聯
//	@ManyToOne
//	@JoinColumn(name = "member_id")
//	private Members members;
	
	private Integer memberId;
	
	
	private String memberName;
	
	
	
	
	
	
	
	@JsonIgnore // 不要 JSON 序列化本屬性，通常用在 Entity 的關聯
	@ManyToOne
	@JoinColumn(name = "postId")
	private Post post;
	
	private String postName;
	
	private Boolean messageShow ;
	
	private Boolean messageDeleteState ;


	
	@PrePersist  // 當物件要轉換成 Persistent 狀態以前，先做以下方法
	public void onCreate() {
		if(messageDate==null) {
		messageDate = new Date();
		}
	}
	public Messages(String messageContent, Integer memberId, String memberName, Post post) {
		this.messageContent = messageContent;
		this.memberId = memberId;
		this.memberName = memberName;
		this.post = post;
		this.postName = post.getPostName();
		this.messageShow = true;
		this.messageDate = new Date();
	}


}
