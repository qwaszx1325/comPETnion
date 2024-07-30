package com.pet.repository.memberChat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.member.Members;
import com.pet.model.memberChat.MemberChat;

public interface MemberChatRepository extends JpaRepository<MemberChat, Integer> {
	
    @Query("FROM MemberChat m WHERE m.senderId = :senderId AND m.receiverId = :receiverId ORDER BY m.timestamp DESC")
    List<MemberChat> findMessages(@Param("senderId") Members senderId, @Param("receiverId") Members receiverId);
    

}
