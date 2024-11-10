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

	@Query("FROM MemberChat mc WHERE mc.senderId = :user OR mc.receiverId = :user ORDER BY mc.timestamp DESC")
	List<MemberChat> findAllChatsForUser(@Param("user") Members user);

	@Query("FROM MemberChat mc WHERE (mc.senderId = :userId AND mc.receiverId = :otherUserId) OR (mc.senderId = :otherUserId AND mc.receiverId = :userId) ORDER BY mc.timestamp DESC")
	List<MemberChat> findLatestMessages(@Param("userId") Integer userId, @Param("otherUserId") Integer otherUserId);

	@Query("SELECT mc FROM MemberChat mc " + "WHERE (mc.senderId = :sender AND mc.receiverId = :receiver) "
			+ "OR (mc.senderId = :receiver AND mc.receiverId = :sender) " + "ORDER BY mc.timestamp ASC")
	List<MemberChat> findConversation(@Param("sender") Members sender, @Param("receiver") Members receiver);

}
