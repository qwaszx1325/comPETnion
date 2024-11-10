package com.pet.service.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.dto.event.EventDto;
import com.pet.model.event.Event;
import com.pet.model.event.Participant;
import com.pet.model.event.ParticipantId;
import com.pet.model.member.Members;
import com.pet.repository.event.ParticipantRepository;

@Service
public class ParticipantService {
	
	@Autowired
	private ParticipantRepository participantRepository;
	
	//新增
//	public Participant saveParticipant(Participant participantBean) {
//		return participantRepository.save(participantBean);
//	}
	
	public Participant saveParticipant(Participant participantBean, Integer eventId, Integer memberId) {
		Participant participant = new Participant();
		
		participant.setEventId(eventId);
		
		participant.setMemberId(memberId);
		
		participant.setMemberName(participantBean.getMemberName());
		participant.setPhone(participantBean.getPhone());
		participant.setEmail(participantBean.getEmail());
		participant.setPet(participantBean.getPet());
		participant.setRegistrationTime(java.sql.Timestamp.valueOf(LocalDateTime.now()));
		participant.setRegistrationUpdateTime(null);
		participant.setRegistrationStatus(0);
		
		return participantRepository.save(participant);
	}
	
	

	
	//刪除
	public void deleteParticipantByParticipantId(Integer participantId) {
		participantRepository.deleteById(participantId);
	}
	
	//修改參加者資料
	public void updateParticipant(Integer participantId, Participant participantUpdateBean) {
		Optional<Participant> optional = participantRepository.findById(participantId);
		if (optional.isPresent()) {
			Participant participantBean = optional.get();
			participantBean.setMemberName(participantUpdateBean.getMemberName());
			participantBean.setPhone(participantUpdateBean.getPhone());
			participantBean.setEmail(participantUpdateBean.getEmail());
			participantBean.setPet(participantUpdateBean.getPet());
			participantBean.setRegistrationTime(participantBean.getRegistrationTime());
			participantBean.setRegistrationUpdateTime(java.sql.Timestamp.valueOf(LocalDateTime.now()));
			participantBean.setRegistrationStatus(2);
			
			participantRepository.save(participantBean);
		}else {
            // 如果找不到對應的參與者，可以選擇拋出異常或者進行其他處理
            throw new RuntimeException("Participant not found with id: " + participantId.toString());
        }
		
	}
	
	//修改報名狀態
	public void updateRegistrationStatus(Integer participantId, Integer registrationStatus) {
		Optional<Participant> optional = participantRepository.findById(participantId);
		if (optional.isPresent()) {
			Participant participant = optional.get();
			participant.setRegistrationUpdateTime(java.sql.Timestamp.valueOf(LocalDateTime.now()));
			participant.setRegistrationStatus(registrationStatus);
			participantRepository.save(participant);
		}
	}
	
	//查詢整張表
	
	//id查詢
	public Participant findParticipantByParticipantId(Integer participantId) {
		Optional<Participant> optional = participantRepository.findById(participantId);
		if (optional.isEmpty()) {
			return null;
		}
			return optional.get();
	}
	
	//查詢特定活動的參加者清單
	public List<Participant> findParticipantsByEvent(Integer eventId){
		return participantRepository.findByEventId(eventId);
	}
	
	//查詢不包含取消報名的參加者清單
	public List<Participant> findByEventIdAndRegistrationStatusNot(Integer eventId, Integer registrationStatus){
		return participantRepository.findByEventIdAndRegistrationStatusNot(eventId, registrationStatus);
	}
	
	//查詢特定會員有參加的活動
	public List<EventDto> findEventDtoByMemberId(Integer memberId){
		List<Participant> participants = participantRepository.findByMemberId(memberId);
		List<EventDto> eventDtos = new ArrayList<>();
		
		for(Participant participant : participants) {
			Event event = participant.getEvent();
			Members hostMember = event.getHostMember();
			String hostMemberName = hostMember.getMemberName();
			
			EventDto eventDto = new EventDto(event, hostMemberName,participant);
			eventDtos.add(eventDto);
			
		}
		return eventDtos;
	}
	

}
