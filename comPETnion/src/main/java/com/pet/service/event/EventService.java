package com.pet.service.event;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.model.event.Event;
import com.pet.model.event.Participant;
import com.pet.model.member.Members;
import com.pet.repository.event.EventRepository;
import com.pet.repository.member.MembersRepository;
import com.pet.service.member.MembersService;
import com.pet.utils.SendMail;

import cn.hutool.core.text.csv.CsvWriter;
import jakarta.mail.MessagingException;

@Service
public class EventService {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private SendMail sendMail;

	@Autowired
	private ParticipantService participantService;

	// 新增
	public Event saveEvent(Event eventBean) {
		return eventRepository.save(eventBean);
	}

	// 刪除
	public void deleteEventByEventId(Integer eventId) {
		eventRepository.deleteById(eventId);
	}

	// 修改
	public Event updateEvent(Event eventBean) {
		Event resultBean = eventRepository.findById(eventBean.getEventId()).orElse(null);

		if (resultBean != null) {

			return eventRepository.save(resultBean);
		}
		return null;

	}

	// ID查詢
	public Event findEventByEventId(Integer eventId) {
		Optional<Event> optional = eventRepository.findById(eventId);

		if (optional.isEmpty()) {
			return null;
		}
		return optional.get();
	}

	// 查詢全部
	public List<Event> findAllEvents() {
		return eventRepository.findAll();
	}
	
	//前台根據不同狀態的分類搜尋
	public List<Event> findByEventStatusAndEventCancelStatus(Integer eventStatus, Integer eventCancelStatus) {
		return eventRepository.findByEventStatusAndEventCancelStatus(eventStatus, eventCancelStatus);
	}
	
	

	// 更新eventStatus
	public void updateEventStatus(Integer eventId, Integer approvalStatus) {
		Optional<Event> eventOptional = eventRepository.findById(eventId);

		if (eventOptional.isPresent()) {
			Event event = eventOptional.get();
			if (approvalStatus != null) {

				if (approvalStatus == 1 || approvalStatus == null) {
					event.setEventStatus(0);
				} else if (approvalStatus == 3) {
					event.setEventStatus(null);
				} else if (approvalStatus == 2) {
					// 根據日期決定是取消還是報名中
					Date eventDate = event.getEventDate();
					Date closingDate = event.getClosingDate();

					if (eventDate.before(new Date())) {
						event.setEventStatus(3);// 活動已結束
					} else if (closingDate.before(new Date()) && eventDate.after(new Date())) {
						event.setEventStatus(2);// 報名截止
					} else if (closingDate.after(new Date())) {
						Integer remainingCapacity = event.getRemainingCapacity();
						if (remainingCapacity > 0) {
							event.setEventStatus(1); // 報名中
						} else {
							event.setEventStatus(4);
						}
					}
				}
			} else {
				event.setEventStatus(0);
			}
			eventRepository.save(event);
		}
	}

	// 更新approvalStatus
	public void updateApprovalStatus(Integer eventId, Integer approvalStatus) {
		Optional<Event> eventOptional = eventRepository.findById(eventId);

		if (eventOptional.isPresent()) {
			Event event = eventOptional.get();
			event.setApprovalStatus(approvalStatus);
			eventRepository.save(event);
		}
	}

	// 更新eventCancelStatus
	public void updateEventCancelStatus(Integer eventId, Integer eventCancelStatus) {
		Optional<Event> eventOptional = eventRepository.findById(eventId);

		if (eventOptional.isPresent()) {
			Event event = eventOptional.get();
			event.setEventCancelStatus(eventCancelStatus);
			eventRepository.save(event);
		}
	}

	// 查詢特定主辦舉辦的活動清單
	public List<Event> findEventByHostMemberId(Integer hostMemberId) {
		return eventRepository.findByHostMemberId(hostMemberId);
	}

	// 依據取消狀態和主辦找尋活動清單
	public List<Event> findByHostMemberIdAndEventCancelStatus(Integer hostMemberId, Integer eventCancelStatus) {
		return eventRepository.findByHostMemberIdAndEventCancelStatus(hostMemberId, eventCancelStatus);
	}

	// 更新報名餘額
	public void updateRemainingCapacity(Integer eventId) {
		Optional<Event> eventOptional = eventRepository.findById(eventId);
		if (eventOptional.isPresent()) {
			List<Participant> participantsByEvent = participantService.findByEventIdAndRegistrationStatusNot(eventId,
					1);
			int participantsCount = participantsByEvent.size();
			Event event = eventOptional.get();
			Integer numberOfParticipant = event.getNumberOfParticipant();
			event.setRemainingCapacity(numberOfParticipant - participantsCount);
			eventRepository.save(event);
		}
	}

	// 比較更新後修改了哪些部分
	private String compareEvents(Event originalEvent, Event updateEvent) {
		StringBuilder changes = new StringBuilder();

		if (!originalEvent.getEventSubject().equals(updateEvent.getEventSubject())) {
			changes.append("活動主題由 '").append(originalEvent.getEventSubject()).append("' 修改為 '")
					.append(updateEvent.getEventSubject()).append("'. ");
		}

		if (!originalEvent.getEventContent().equals(updateEvent.getEventContent())) {
			changes.append("活動內容由 '").append(originalEvent.getEventContent()).append("' 修改為 '")
					.append(updateEvent.getEventContent()).append("'. ");
		}

		if (!originalEvent.getEventDate().equals(updateEvent.getEventDate())) {
			changes.append("活動日期由 '").append(originalEvent.getEventDate()).append("' 修改為 '")
					.append(updateEvent.getEventDate()).append("'. ");
		}

		if (!originalEvent.getEventPlaceName().equals(updateEvent.getEventPlaceName())) {
			changes.append("活動地點由 '").append(originalEvent.getEventPlaceName()).append("' 修改為 '")
					.append(updateEvent.getEventPlaceName()).append("'. ");
		}

		if (!originalEvent.getEventPlaceAddress().equals(updateEvent.getEventPlaceAddress())) {
			changes.append("活動地址由 '").append(originalEvent.getEventPlaceAddress()).append("' 修改為 '")
					.append(updateEvent.getEventPlaceAddress()).append("'. ");
		}

		if (!originalEvent.getEventPlaceAddress().equals(updateEvent.getEventPlaceAddress())) {
			changes.append("活動地址由 '").append(originalEvent.getEventPlaceAddress()).append("' 修改為 '")
					.append(updateEvent.getEventPlaceAddress()).append("'. ");
		}

		return changes.toString();
	}

	private String formatDate(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	private static final Map<Integer, String> statusMap = new HashMap<>();

	static {
		statusMap.put(0, "成功報名");
		statusMap.put(1, "取消報名");
		statusMap.put(2, "修改後成功報名");
	}

	private String getStatusString(Integer registrationStatus) {
        return statusMap.getOrDefault(registrationStatus, ""); // 如果狀態碼無效，返回空字符串
    }
	
	
	
	// 生成csv檔
	@Transactional
	public String generateCsv(List<Participant> participantList) {
		StringWriter writer = new StringWriter();
		writer.write("\uFEFF");
		CsvWriter csvWriter = new CsvWriter(writer);
		// 寫入CSV標題行
		String[] header = { "會員名稱", "連絡電話", "E-MAIL", "寵物詳情", "報名時間", "報名資料更新時間", "報名狀態" };
		csvWriter.writeHeaderLine(header);

		// 將每個Participant對象寫入CSV文件
		for (Participant participant : participantList) {
			String[] data = { participant.getMemberName(), participant.getPhone(), participant.getEmail(),
					participant.getPet() != null ? participant.getPet() : "", // 處理可能的null值
					formatDate(participant.getRegistrationTime()), 
					formatDate(participant.getRegistrationUpdateTime()), 
					getStatusString(participant.getRegistrationStatus())
					};
					
			csvWriter.write(data);
		}

		return writer.toString();
	}

//    public String generateCsv(List<Participant> participantList) {
//    	StringWriter writer = new StringWriter();
//            // 寫入CSV標題行
//            writer.write("會員名稱,連絡電話,E-MAIL,寵物詳情,報名時間,報名資料更新時間,報名狀態\n");
//            
//            // 將每個Participant對象寫入CSV文件
//            for (Participant participant : participantList) {
//                writer.write(participant.getMemberName() + "," + participant.getPhone() + "," + participant.getEmail() + "," + 
//                		participant.getPet() + "," + participant.getRegistrationTime() + "," + participant.getRegistrationUpdateTime() + "," + 
//                		participant.getRegistrationStatus() + "\n");
//            }
//            
//            return writer.toString();
//        } 

	
	//找出特定截止日期的活動清單
	@Transactional
	public List<Event> findEventsByClosingDate(){
//		LocalDate today = LocalDate.now();
//         將今天的日期轉換為當天的開始時間
//        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        
        
        // 將 LocalDateTime 轉換為 Timestamp
        Timestamp timestamp = Timestamp.valueOf(startOfYesterday);
		
//		return eventRepository.findByClosingDate(timestamp);
		return eventRepository.findByClosingDateAndEventCancelStatusAndApprovalStatus(timestamp,0,2);
	}
	
	@Transactional
	public void sendParticipantListAfterClosing(Integer eventId) throws MessagingException {
		Optional<Event> optional = eventRepository.findById(eventId);
		Event event = optional.get();

		if (event != null) {
            List<Participant> participantsByEvent = participantService.findParticipantsByEvent(eventId);
			String csvContent = generateCsv(participantsByEvent);
			String eventSubject = event.getEventSubject();
			String hostMemberEmail = event.getHostMember().getMemberEmail();

			sendMail.sendParticipantList(hostMemberEmail, eventId, eventSubject, csvContent);
		}

	}

}
