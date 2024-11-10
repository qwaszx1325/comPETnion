package com.pet.model.adopt;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pet.model.member.Members;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "application")
public class Application {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_generator")
	@SequenceGenerator(name = "seq_generator", sequenceName = "application_seq", initialValue = 1001, allocationSize = 1)
	private Integer applyId;

	@ManyToOne
	@JoinColumn(name = "memberId")
	private Members members;

	public void setMembersById(Integer memberId) {
		this.members = new Members();
		this.members.setMemberId(memberId);
	}

	@Column(nullable = false)
	private String applicantName;

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date applyTime;

	@Column(nullable = false)
	private String agreement;

	@Column(nullable = false)
	private String applyReason;

	@ManyToOne
	@JoinColumn(name = "petCaseId")
	private Adoptions adoptions;

	public void setAdoptionsById(Integer petCaseId) {
		this.adoptions = new Adoptions();
		this.adoptions.setPetCaseId(petCaseId);
	}

	private boolean ownerRespond = false;
	private String applyStatus;

}
