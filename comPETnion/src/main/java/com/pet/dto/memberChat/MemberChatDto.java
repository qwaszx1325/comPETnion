package com.pet.dto.memberChat;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MemberChatDto {

	private Integer senderId;
	private Integer receiverId;
	private String senderName;
	private String receiverName;
	private String content;
	private Date timestamp;
	
	// 新增的字段，用於對話列表
    private Integer userId;
    private String userName;
    private String lastMessage;
}
