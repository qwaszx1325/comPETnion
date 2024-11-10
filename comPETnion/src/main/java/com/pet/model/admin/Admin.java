package com.pet.model.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;


@Entity @Table(name = "admin")
@Component
public class Admin {

	@Id @Column(name="adminId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer adminId;
	
	@Column(name = "adminName")
	private String adminName;
	
	@Column(name = "adminEmail")
	private String adminEmail;
	
	@Column(name = "adminPassword")
	private String adminPassword;

	

	public Admin() {

	}
	public Integer getAdminId() {
		return adminId;
	}



	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}



	public String getAdminName() {
		return adminName;
	}



	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}



	public String getAdminEmail() {
		return adminEmail;
	}



	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}



	public String getAdminPassword() {
		return adminPassword;
	}



	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}



	
   
}
