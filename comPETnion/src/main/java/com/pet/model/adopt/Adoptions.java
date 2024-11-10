package com.pet.model.adopt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pet.model.member.Members;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "adoptions")
public class Adoptions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer petCaseId;

	@ManyToOne
	@JoinColumn(name = "memberId")
	private Members members;

	@Column(nullable = false)
	private String petName;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "adoptions")
	private List<PetPhoto> petPhoto = new ArrayList<>();

	@Column(nullable = false)
	private String petCity;

	@Column(nullable = false)
	private String petDistrict;

	@Column(nullable = false)
	private String petKind;

	@Column(nullable = false)
	private String petBreed;

	@Column(nullable = false)
	private String petColour;

	@Column(nullable = false)
	private String petSex;

	@Column(nullable = false)
	private String petBodyType;

	@Column(nullable = false)
	private String petAge;

	@Column(nullable = false)
	private String medicalRecord;

	@Column(nullable = false)
	private String petDescription;
	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date petPostDate;

	@PrePersist
	public void onCreate() {
		if (petPostDate == null) {
			petPostDate = new Date();
		}
	}

	@Column(nullable = false)
	private Integer caseStatus;
	
	private boolean autoShareToFb;

}
