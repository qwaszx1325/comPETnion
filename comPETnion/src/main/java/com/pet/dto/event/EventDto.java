package com.pet.dto.event;

import com.pet.model.event.Event;
import com.pet.model.event.Participant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EventDto {

	private Event event;
	private String memberName;
	private Participant participant;
	

	public EventDto(Event event, String memberName, Participant participant) {
		super();
		this.event = event;
		this.memberName = memberName;
		this.participant = participant;
	}
	
	
	
	
}
