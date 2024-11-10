package com.pet.controller.event;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pet.model.event.Event;
import com.pet.service.event.EventService;

import jakarta.mail.MessagingException;


@Component
@EnableScheduling
public class ScheduledTasks {
	
	@Autowired
	private EventService eventService;
	
	@Scheduled(cron = "0 0 0 * * *")
	public void sendParticipantListAfterClosing() {
		
		List<Event> events  = eventService.findEventsByClosingDate();
		if (events!=null) {
			for(Event event :events) {
				try {
					eventService.sendParticipantListAfterClosing(event.getEventId());
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	
	
	
	

}
