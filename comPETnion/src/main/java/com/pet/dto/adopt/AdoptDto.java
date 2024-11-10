package com.pet.dto.adopt;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AdoptDto {
	
	private Integer memberId; 
	private Integer petCaseId;
    private String petName;
    
    // 照片
    private MultipartFile[] petPhotoFile;
    private List<Integer> deletePhotoIds;
    
    private String petCity;
    private String petDistrict;
    private String petKind;
    private String petBreed;
    private String petColour;
    
    private String petSex;
    private String petBodyType;
    private String petAge;
    private String medicalRecord;
    private String petDescription;
    
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd") 
    @Temporal(TemporalType.TIMESTAMP)
    private Date petPostDate;
    @PrePersist
    public void onCreate() {
		if (petPostDate == null) {
			petPostDate = new Date();
		}
	}
    
    private int caseStatus;
    private boolean autoShareToFb;

}
