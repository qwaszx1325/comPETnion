package com.pet.model.forum;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@Setter
@Getter
@Entity 
@Table(name = "theme")
public class Theme {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer themeId;
	private String themeName;
	
	
	@Column(length = Integer.MAX_VALUE)
	private String themeDescription;

	
	@JsonIgnore
	@Lob
	private byte[] forumPhoto;

	
	
	

	private Boolean isShow;
	
	private Integer themeViews;

	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "theme")
    private List<Post> post = new ArrayList<>();
	
	public Theme(Integer themeId, String themeName, String themeDescription) {
		super();
		this.themeId = themeId;
		this.themeName = themeName;
		this.themeDescription = themeDescription;
		
	}







	




	



}
