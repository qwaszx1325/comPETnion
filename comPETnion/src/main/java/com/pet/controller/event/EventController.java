package com.pet.controller.event;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pet.model.event.Event;
import com.pet.model.event.Participant;
import com.pet.model.member.Members;
import com.pet.repository.event.EventRepository;
import com.pet.service.event.EventService;
import com.pet.service.event.ParticipantService;
import com.pet.service.member.MembersService;
import com.pet.utils.SendMail;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
public class EventController {
	
	@Autowired
	EventService eventService;
	
	@Autowired
	private EventRepository eventRepository;

	@Autowired
	ParticipantService participantService;
	
	@Autowired
	SendMail sendMail;
	
	@Value("${google.maps.api.key}")
	private String googleMapsApiKey;

	//新增表單頁面
	@GetMapping("/member/addEventForm")
	public String addEventForm(HttpSession session, Model model) {
		if (session.getAttribute("member") == null) {
			return "redirect:/public/login";
		}
		model.addAttribute("apiKey", googleMapsApiKey);
		return "event/addEventForm";
	}
	
	//新增
	@PostMapping("/member/addEvent.controller")
	public String insertEvent(@RequestParam("memberId") Integer memberId,
			@RequestParam("eventSubject") String eventSubject,
			@RequestParam("eventContent") String eventContent, 
			@RequestParam("eventDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date eventDate,
			@RequestParam("eventPlaceName") String eventPlaceName,
			@RequestParam("eventPlaceAddress") String eventPlaceAddress,
			@RequestParam("eventPlaceLatitude") Double eventPlaceLatitude,
			@RequestParam("eventPlaceLongitude") Double eventPlaceLongitude,
			@RequestParam("numberOfParticipant") Integer numberOfParticipant,
			@RequestParam("closingDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date closingDate, 
			@RequestParam("eventImg") MultipartFile mf, Model model)
			throws IllegalStateException, IOException {
		String fileName = mf.getOriginalFilename();
		byte[] b = mf.getBytes();

		if (eventDate.before(closingDate)) {
			model.addAttribute("error","舉辦時間不能早於報名截止日期");
			return "event/addEventForm";
		}
		
		if (fileName != null && fileName.length() != 0) {
			Event eventBean = new Event();
			eventBean.setHostMemberId(memberId);
			eventBean.setEventSubject(eventSubject);
			eventBean.setEventContent(eventContent);
			eventBean.setEventDate(eventDate);
			eventBean.setEventPlaceName(eventPlaceName);
			eventBean.setEventPlaceAddress(eventPlaceAddress);
			eventBean.setEventPlaceLatitude(eventPlaceLatitude);
			eventBean.setEventPlaceLongitude(eventPlaceLongitude);
			eventBean.setNumberOfParticipant(numberOfParticipant);
			eventBean.setRemainingCapacity(numberOfParticipant);

			eventBean.setReleaseDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));
			eventBean.setUpdateDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));

			eventBean.setClosingDate(closingDate);
			eventBean.setEventImg(b);
			eventBean.setApprovalStatus(1);
			eventBean.setEventCancelStatus(0);
			eventService.saveEvent(eventBean);
		}
		return "redirect:/member/holdEventList";
	}
	
	//刪除
	@DeleteMapping("/member/deleteEvent.controller")
	public String deleteEvent(@RequestParam Integer eventId) {
		eventService.deleteEventByEventId(eventId);
		return "redirect:/getAllEvents";
	}
	
	//修改表單
	@GetMapping("/member/updateEventForm")
	public String updateEventForm(@RequestParam("eventId") Integer eventId, Model model) {
		Event eventBean = eventService.findEventByEventId(eventId);
		model.addAttribute("eventBean", eventBean);
		model.addAttribute("apiKey", googleMapsApiKey);
		return "event/updateEventForm";
	}
	
	//修改
	@PutMapping("/member/updateEvent.controller")
	public String updateEvent(@RequestParam("eventId") Integer eventId,
	        @RequestParam("eventSubject") String eventSubject,
	        @RequestParam("eventContent") String eventContent, 
	        @RequestParam("eventDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date eventDate,
	        @RequestParam("eventPlaceName") String eventPlaceName,
			@RequestParam("eventPlaceAddress") String eventPlaceAddress,
			@RequestParam("eventPlaceLatitude") Double eventPlaceLatitude,
			@RequestParam("eventPlaceLongitude") Double eventPlaceLongitude,
	        @RequestParam("numberOfParticipant") Integer numberOfParticipant,
	        @RequestParam("closingDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date closingDate, 
	        @RequestParam(value = "eventImg", required = false) MultipartFile mf,
	        @RequestParam(value = "keepOriginal", defaultValue = "false") boolean keepOriginal, Model model)
	        throws IllegalStateException, IOException {

	    Event eventBean = eventService.findEventByEventId(eventId);
	    eventBean.setApprovalStatus(1);
	    eventBean.setEventSubject(eventSubject);
	    eventBean.setEventContent(eventContent);
	    eventBean.setEventDate(eventDate);
	    eventBean.setEventPlaceName(eventPlaceName);
		eventBean.setEventPlaceAddress(eventPlaceAddress);
		eventBean.setEventPlaceLatitude(eventPlaceLatitude);
		eventBean.setEventPlaceLongitude(eventPlaceLongitude);
	    eventBean.setNumberOfParticipant(numberOfParticipant);
	    eventBean.setClosingDate(closingDate);
	    LocalDate now = LocalDate.now();
	    eventBean.setUpdateDate(java.sql.Timestamp.valueOf(LocalDateTime.now()));

	    if (keepOriginal) {
	        // 不更新 eventImg 欄位
	    }else if(!keepOriginal && mf != null && !mf.isEmpty()){
	    	byte[] b = mf.getBytes();
	        eventBean.setEventImg(b);
	    }else {
	    	eventBean.setEventImg(null);
	    }
	    
	    eventService.updateEvent(eventBean);
	    model.addAttribute("eventBean", eventBean);
	    return "redirect:/member/holdEventList";
	}

	//ID查詢
	@GetMapping("/public/findEventById.controller")
	public String findEventById(@RequestParam("eventId") Integer eventId, HttpSession session, Model model) {
		Event eventBean = eventService.findEventByEventId(eventId);
		Members member =(Members) session.getAttribute ("member");
		if(member!=null) {
			model.addAttribute("member", member);
		}
		
		model.addAttribute("googleMapsApiKey",googleMapsApiKey);
		model.addAttribute("eventBean", eventBean);

		return "event/getOneEvent";
	}
	
	//後臺單一查詢
	@GetMapping("/admin/findEventByIdBack.controller")
	public String findEventByIdBack(@RequestParam("eventId") Integer eventId, Model model) {
		Event eventBean = eventService.findEventByEventId(eventId);
		model.addAttribute("eventBean", eventBean);
		return "event/oneEventBack";
	}
	
	//關鍵字查詢
	@GetMapping("/public/findEventByKeyword.controller")
	public String findEventByKeyword(@RequestParam("keyword") String keyword, Model model) {
		List<Event> eventBeans = eventRepository.findEventByKeyword(keyword);
		model.addAttribute("eventBeans", eventBeans);
		return "event/getEventsResult";
	}
	
	//後臺查詢未取消活動
	@GetMapping("/admin/findNotCancelEvents.controller")
	public String findNotCancelEvents(Model model) {
		List<Event> eventBeans = eventRepository.findNotCancelEvents();
		for(Event event : eventBeans) {
			eventService.updateRemainingCapacity(event.getEventId());
        	eventService.updateEventStatus(event.getEventId(), event.getApprovalStatus());
        }
		model.addAttribute("eventBeans", eventBeans);
		return "event/indexBackEvent";
	}
	
	//後臺查詢取消活動
	@GetMapping("/admin/findCancelEvents.controller")
	public String findCancelEvents(Model model) {
		List<Event> eventBeans = eventRepository.findCancelEvents();
		for(Event event : eventBeans) {
        	eventService.updateEventStatus(event.getEventId(), event.getApprovalStatus());
        }
		model.addAttribute("eventBeans", eventBeans);
		return "event/indexBackEventCancel";
	}
	
	
	//後台查詢全部
	@GetMapping("/admin/getAllEvents")
    public String getAllEvents(Model model) {
        List<Event> eventBeans = eventService.findAllEvents();

        for(Event event : eventBeans) {
        	eventService.updateEventStatus(event.getEventId(), event.getApprovalStatus());
        }
        model.addAttribute("eventBeans", eventBeans);

        return "event/indexBackEvent";
    }
	
	//全部活動json
	@ResponseBody
    @GetMapping("/json/events")
    public List<Event> getAllEvents() {
        return eventService.findAllEvents();
    }
	
	//前台首頁
	@GetMapping("/public/frontAllEvents")
	public String getAllEventsFront(Model model) {
	    List<Event> eventBeans = eventRepository.findEventFront();
	    model.addAttribute("eventBeans", eventBeans);
	    return "event/indexFrontEvent"; 
	}
	
	//前台報名中
	@GetMapping("/public/registrationOpen")
	public String getRegistrationOpenEvents(Model model) {
	    List<Event> eventBeans = eventService.findByEventStatusAndEventCancelStatus(1,0);
	    model.addAttribute("eventBeans", eventBeans);
	    return "event/indexFrontEvent"; 
	}
	
	//前台已結束活動
	@GetMapping("/public/endEvents")
	public String getEndEvents(Model model) {
	    List<Event> eventBeans = eventService.findByEventStatusAndEventCancelStatus(3,0);
	    model.addAttribute("eventBeans", eventBeans);
	    return "event/indexFrontEvent"; 
	}
	
	
//	更新eventCancelStatus
	@PutMapping("/eventCancel")
	public String eventCancel(@RequestParam("eventId") Integer eventId, 
			@RequestParam("eventCancelStatus") Integer eventCancelStatus,@RequestParam("frontOrEnd") String frontOrEnd) {
		eventService.updateEventCancelStatus(eventId, eventCancelStatus);
		if(eventCancelStatus==1) {
			Event event = eventService.findEventByEventId(eventId);
			List<Participant> participants = event.getParticipants();
		
			List<String> emailList = new ArrayList<>();
			for(Participant participant : participants) {
				emailList.add(participant.getEmail());
			}
			try {
				sendMail.sendEventCancel(emailList, event.getHostMember().getMemberName(), event.getEventSubject(), event.getEventDate());
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if("end".equals(frontOrEnd)) {
			return "redirect:/admin/findNotCancelEvents.controller";
		}
			return "redirect:/member/holdEventList";
	}
	
//	更新Status
	@PutMapping("/updateStatus")
	public String updateStatus(@RequestParam("eventId") Integer eventId, 
			@RequestParam("approvalStatus") Integer approvalStatus, Model model) {
		eventService.updateApprovalStatus(eventId, approvalStatus);
		eventService.updateEventStatus(eventId, approvalStatus);
		Event event = eventService.findEventByEventId(eventId);
		Members hostMember = event.getHostMember();
		String memberEmail = hostMember.getMemberEmail();
		String hostMemberName = hostMember.getMemberName();
		String eventSubject = event.getEventSubject();
		if(approvalStatus!=null && approvalStatus!=1) {
			try {
				sendMail.sendEventApproval(memberEmail, eventId, eventSubject, approvalStatus);
				model.addAttribute("message", "審核通知已發送");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				model.addAttribute("error","發送郵件通知失敗:" + e.getMessage());
				e.printStackTrace();
			}
		}
		if(approvalStatus==2 && event.getParticipants()!=null) {
			List<Participant> participants = event.getParticipants();
			List<String> emailList = new ArrayList<>();
			for(Participant participant : participants) {
				emailList.add(participant.getEmail());
			}
			try {
				sendMail.sendEventUpdate(emailList, hostMemberName, eventSubject);
				model.addAttribute("message", "審核通知已發送");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				model.addAttribute("error","發送郵件通知失敗:" + e.getMessage());
				e.printStackTrace();
			}
		}
			
		
		return "redirect:/admin/findNotCancelEvents.controller";
	}
	
	//查詢特定主辦舉辦的活動清單
	@GetMapping("/member/holdEventList")
	public String holdEventList(HttpSession session, Model model) {
		Members member =(Members) session.getAttribute ("member");
		if(member==null) {
			return "redirect:/public/login";
		}
		Integer hostMemberId = member.getMemberId();
		List<Event> eventBeans = eventService.findByHostMemberIdAndEventCancelStatus(hostMemberId,0);
		for(Event event : eventBeans) {
			eventService.updateRemainingCapacity(event.getEventId());
        	eventService.updateEventStatus(event.getEventId(), event.getApprovalStatus());
        }
		model.addAttribute("eventBeans", eventBeans);
		return "event/holdEventList";
	}
	
	//查詢特定主辦舉辦的已取消活動清單
	@GetMapping("/member/holdEventCancelList")
	public String holdEventCancelList(HttpSession session, Model model) {
		Members member =(Members) session.getAttribute ("member");
		if(member==null) {
			return "redirect:/public/login";
		}
		Integer hostMemberId = member.getMemberId();
		List<Event> eventBeans = eventService.findByHostMemberIdAndEventCancelStatus(hostMemberId,1);
		model.addAttribute("eventBeans", eventBeans);
		return "event/holdEventCancelList";
	}
	
	
//	byte轉網址
	@GetMapping("/photos/download")
	public ResponseEntity<byte[]> downloadPhotos(@RequestParam("eventId") Integer eventId) {
		Event eventBean = eventService.findEventByEventId(eventId);
		
		byte[] photoFile = eventBean.getEventImg();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		                                  //body,    headers, http status code
		return new ResponseEntity<byte[]>(photoFile, headers, HttpStatus.OK);
		 
	}
	
	//google map api
	@GetMapping("/map")
	public String showMap(Model model) {
		model.addAttribute("apiKey", googleMapsApiKey);
		return "event/map";
	}
}
