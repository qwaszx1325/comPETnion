package com.pet.controller.event;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pet.dto.event.EventDto;
import com.pet.model.event.Event;
import com.pet.model.event.Participant;
import com.pet.model.member.Members;
import com.pet.repository.event.ParticipantRepository;
import com.pet.service.event.EventService;
import com.pet.service.event.ParticipantService;
import com.pet.service.member.MembersService;
import com.pet.utils.SendMail;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
public class ParticipantController {

	@Autowired
	ParticipantService participantService;
	
	@Autowired
	EventService eventService;
	
	@Autowired
	MembersService membersService;
	
	@Autowired
	ParticipantRepository participantRepository;
	
	@Autowired
	SendMail sendMail;
	
	//報名表單
	@GetMapping("/member/registrationForm")
	public String registrationForm(@RequestParam("eventId") Integer eventId, HttpSession session, Model model) {
		if (session.getAttribute("member") == null) {
			return "redirect:/members/login";
		}
		
		Event event = eventService.findEventByEventId(eventId);
		Members member = (Members)session.getAttribute("member");
		Integer memberId = member.getMemberId();
		if(memberId != null) {
			model.addAttribute("member", member);
		}
		model.addAttribute("event", event);
		model.addAttribute("participant", new Participant());//創建空的Participant物件
		
		return "event/registrationForm";
	}
	
	//報名
//	@PostMapping("registration.controller")
//	public String signUp(@RequestParam("memberId") Integer memberId, 
//			@RequestParam("eventId") Integer eventId, 
//			@RequestParam("memberName") String memberName,
//			@RequestParam("phone") String phone, 
//			@RequestParam("email") String email,
//			@RequestParam("pet") String pet, 
//			HttpSession session, Model model) {
//		Participant participant = new Participant();
////		ParticipantId participantId = new ParticipantId(eventId, memberId);
//
////		participant.setParticipantId(participantId);
//		Event event = eventService.findEventByEventId(eventId);
//		Members member = (Members)session.getAttribute("member");
////		participant.setEvent(event);
//		participant.setEventId(eventId);
////		Members member = membersService.findMembersById(memberId);
//		System.out.println(member);
////		participant.setMember(member);
//		participant.setMemberId(memberId);
//		participant.setMemberName(memberName);
//		participant.setPhone(phone);
//		participant.setEmail(email);
//		participant.setPet(pet);
//		participant.setRegistrationTime(java.sql.Timestamp.valueOf(LocalDateTime.now()));
//		participant.setRegistrationUpdateTime(null);
//		participant.setRegistrationStatus(0);
//		participantService.saveParticipantt(participant);
//		return "redirect:frontAllEvents";
//	}
//	
	
	//報名
	@PostMapping("member/registration.controller")
	public String signUp(@ModelAttribute("participant") Participant participant,
			@RequestParam("eventId") Integer eventId, @RequestParam("memberId") Integer memberId,
			HttpSession session) {
//		Integer memberId = (Integer)session.getAttribute("memberId");
		Members member =(Members) session.getAttribute ("member");
		participantService.saveParticipant(participant, eventId, memberId);  
		eventService.updateRemainingCapacity(eventId);
		Event event = eventService.findEventByEventId(eventId);
		eventService.updateEventStatus(eventId, event.getApprovalStatus());
		try {
			sendMail.sendRegistrationSuccess(member.getMemberEmail(), event.getHostMember().getMemberName(), event.getEventSubject(), event.getEventDate(), event.getEventPlaceName(), event.getEventPlaceAddress());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "redirect:/member/findEventDtoByMemberId.controller";
	}
	
	//修改報名狀態(包括取消報名)
	@PutMapping("member/updateRegistrationStatus")
	public String updateRegistrationStatus(@RequestParam("participantId") Integer participantId, 
			@RequestParam("registrationStatus") Integer registrationStatus) {
		participantService.updateRegistrationStatus(participantId, registrationStatus);
		return "redirect:/member/findEventDtoByMemberId.controller";
	}
	
	//修改報名資料表單
	@GetMapping("member/updateRegistrationInfoForm")
	public String updateRegistrationInfoForm(@RequestParam("participantId") Integer participantId, HttpSession session,
			Model model) {
		Participant participantBean = participantService.findParticipantByParticipantId(participantId);
		Event event = participantBean.getEvent();
		Members member = (Members)session.getAttribute("member");
		if(member == null) {
			return "redirect:/public/login";
		}
		model.addAttribute("member", member);
		model.addAttribute("participantBean", participantBean);
		model.addAttribute("event", event);
		return "event/updateRegistrationInfoForm";
	}
	
	//修改報名資料
	@PutMapping("member/updateRegistrationInfo.controller")
	public String updateRegistrationInfo(@ModelAttribute("participant") Participant participantBean,
			@RequestParam("participantId") Integer participantId) {
		participantService.updateParticipant(participantId, participantBean);
		return "redirect:/member/findEventDtoByMemberId.controller";
	}
	
	//ID查詢
	@GetMapping("member/findParticipantById.controller")
	public String findParticipantById(@RequestParam("participantId") Integer participantId, Model model) {
		Participant participant = participantService.findParticipantByParticipantId(participantId);
		Event event = participant.getEvent();
		model.addAttribute("participant", participant);
		model.addAttribute("event", event);
		return "event/registrationInfo";
	}
	
	//查詢特定活動的參加者清單(後台)
	@GetMapping("/admin/findParticipantsByEvent.controller")
	public String findParticipantsByEvent(@RequestParam("eventId") Integer eventId, Model model) {
		List<Participant> participants = participantService.findParticipantsByEvent(eventId);
		Event event = eventService.findEventByEventId(eventId);
		model.addAttribute("event", event);
		model.addAttribute("participants", participants);
		return "event/participantBackTable";
	}
	
	//查詢特定活動的參加者清單(前台)
	@GetMapping("/member/findParticipantsByEvent.controller")
	public String findParticipantsByEventFront(@RequestParam("eventId") Integer eventId, Model model) {
		List<Participant> participants = participantService.findParticipantsByEvent(eventId);
		Event event = eventService.findEventByEventId(eventId);
		model.addAttribute("event", event);
		model.addAttribute("participants", participants);
		return "event/participantFrontTable";
	}
	
	//查詢特定會員有參加的活動
	@GetMapping("/member/findEventDtoByMemberId.controller")
	public String findEventDtoByMemberId(HttpSession session, Model model) {
		Members member =(Members) session.getAttribute ("member");
		if(member==null) {
			return "redirect:/public/login";
		}
		Integer memberId = member.getMemberId();
		List<EventDto> eventDtos = participantService.findEventDtoByMemberId(memberId);
		for(EventDto eventDto : eventDtos) {
			eventService.updateRemainingCapacity(eventDto.getEvent().getEventId());
        	eventService.updateEventStatus(eventDto.getEvent().getEventId(), eventDto.getEvent().getApprovalStatus());
        }
		
		
		model.addAttribute("eventDtos", eventDtos);
		return "event/signUpEventList";
	}
	
	//檢查是否重複報名
	@GetMapping("/member/checkRegistration")
	@ResponseBody
	public Map<String, Boolean> checkRegistration(@RequestParam("eventId") Integer eventId, 
			HttpSession session){
		Members member =(Members) session.getAttribute ("member");
		Integer memberId = member.getMemberId();
		boolean isAlreadyRegistered = participantRepository.existsByEventIdAndMemberIdAndRegistrationStatusNot(eventId, memberId, 1);
		Map<String, Boolean> responseMap = new HashMap<>();
		responseMap.put("registered", isAlreadyRegistered);
		
		return responseMap;
		
	}
}
