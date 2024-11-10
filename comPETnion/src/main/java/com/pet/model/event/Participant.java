package com.pet.model.event;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pet.model.member.Members;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity @Table(name = "participant")
public class Participant {

//	@EmbeddedId
//    private ParticipantId participantId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer participantId;

	
	private String memberName;
	private String phone;
	private String email;
	
	@Column(length = 1000)
	private String pet;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("eventId")
    @JoinColumn(name = "eventId", referencedColumnName = "eventId")
	private Event event;
	
	@Column(name = "eventId")
	private Integer eventId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("memberId")
    @JoinColumn(name = "memberId", referencedColumnName = "memberId")
	private Members member;
	
	@Column(name = "memberId")
	private Integer memberId;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date registrationTime;
	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date registrationUpdateTime;
	
	private Integer registrationStatus;
	
}
