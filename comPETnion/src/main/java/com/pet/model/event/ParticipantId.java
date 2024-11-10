package com.pet.model.event;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Embeddable
public class ParticipantId implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer memberId;
	private Integer eventId;
	
	
	
	@Override
	public int hashCode() {
		return Objects.hash(eventId, memberId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParticipantId other = (ParticipantId) obj;
		return Objects.equals(eventId, other.eventId) && Objects.equals(memberId, other.memberId);
	}

	public ParticipantId(Integer memberId, Integer eventId) {
		this.memberId = memberId;
		this.eventId = eventId;
	}
	
	
	
}

