package com.pet.model.forum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="post")
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private	Integer postId;
	
	private	String postName;
	
	@Column(length = Integer.MAX_VALUE)
	private	String postContent;
	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 若要在 thymeleaf 強制使用本格式，需雙層大括號
	@Temporal(TemporalType.TIMESTAMP)
	private Date postDate;
	
	private Boolean postShow ;
	
	private Boolean postDeleteState ;
	
	private Integer postViews;
	
	private String memberName;
	
	private Integer memberId;


	@JsonIgnore
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<Messages> messages = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "post")
    private List<Forumphotos> forumphotos = new ArrayList<>();
//	private Member member;
	
	    @JsonIgnore
	    @ManyToOne
	    @JoinColumn(name = "themeId")
	    private Theme theme; // 使用 @ManyToOne 和 @JoinColumn 注解定义多对一关系
	
	    public void addMessage(Messages message) {
			this.messages.add(message);
			message.setPost(this);
			message.setPostName(this.postName); // Ensure postName is set
		}
	    
	    private String imageUrl;   
	    
}
