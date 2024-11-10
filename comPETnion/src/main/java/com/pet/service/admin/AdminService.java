package com.pet.service.admin;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import com.pet.model.admin.Admin;

import com.pet.model.member.Members;
import com.pet.repository.admin.AdminRepository;


@Service
public class AdminService {

	@Autowired
	private AdminRepository adminRepo;

	public Admin checkLogin(String adminEmail, String adminPassword) {

		Admin admin = adminRepo.findByAdminEmailAndAdminPassword(adminEmail, adminPassword);

		if (admin == null) {
			return null;
		} else {
			String dbEmail = admin.getAdminEmail();
			String dbPassword = admin.getAdminPassword();
			// 前端adminEmail跟數據庫dbEmail進行比較
			if (adminEmail.equals(dbEmail) && adminPassword.equals(dbPassword)) {
				return admin;
			} else {
				return null;
			}
		}
	}
}
