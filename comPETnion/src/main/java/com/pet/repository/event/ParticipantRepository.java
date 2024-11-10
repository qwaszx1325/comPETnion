package com.pet.repository.event;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.event.Event;
import com.pet.model.event.Participant;


public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

	// 根據活動查詢其參加者名單
	List<Participant> findByEvent(Event event);
	
	List<Participant> findByEventId(Integer eventId);
	
	List<Participant> findByEventIdAndRegistrationStatusNot(Integer eventId, Integer registrationStatus);
	
	//查詢特定會員有參加的活動
//	List<Participant> findByParticipantId_MemberId(Integer memberId);
	
	List<Participant> findByMemberId(Integer memberId);
	
	//確認報名資訊是否存在
	boolean existsByEventIdAndMemberId(Integer eventId, Integer memberId);
	
	boolean existsByEventIdAndMemberIdAndRegistrationStatusNot(Integer eventId, Integer memberId, Integer registrationStatus);
}
