package com.pet.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.admin.Admin;
import com.pet.model.member.Members;

import jakarta.transaction.Transactional;

import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin,Integer> {

	
	@Query("FROM Admin WHERE adminEmail=:adminEmail AND adminPassword=:adminPassword")
	Admin findByAdminEmailAndAdminPassword(String adminEmail,String adminPassword);
	
	 
	
	
//	@Query(value="SELECT * FROM ADMIN WHERE adminEmail=:adminEmail, adminPassword=:adminPassword", nativeQuery=true)
//	Admin loginAdmin(String adminEmail, String adminPassword);

}
