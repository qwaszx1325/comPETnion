package com.pet.service.adopt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pet.dto.adopt.CaseStatDto;
import com.pet.model.adopt.Adoptions;
import com.pet.model.adopt.Application;
import com.pet.model.adopt.PetPhoto;
import com.pet.model.member.Members;
import com.pet.repository.adopt.AdoptRepository;
import com.pet.repository.adopt.ApplicationRepository;
import com.pet.repository.adopt.PetPhotoRepository;
import com.pet.utils.SendMail;

@Service
@EnableScheduling
public class AdoptService {

	@Autowired
	private AdoptRepository adoptRepo;
	@Autowired
	private PetPhotoRepository petPhotoRepo;
	@Autowired
	private ApplicationRepository applicationRepo;
	@Autowired
	private SendMail sendMail;

	// 分頁
	public Page<Adoptions> findByPage(Integer pageNum) {
		Pageable pageable = PageRequest.of(pageNum - 1, 15, Sort.Direction.DESC, "petPostDate");
		Page<Adoptions> page = adoptRepo.findAll(pageable);
		return page;
	}

	// 搜尋的分頁&僅顯示審核過的案件（前台）
	public Page<Adoptions> findByStatus(Integer pageNum, int caseStatus) {
		Pageable pageable = PageRequest.of(pageNum - 1, 15, Sort.Direction.DESC, "petPostDate");
		return adoptRepo.findByCaseStatus(caseStatus, pageable);
	}

	// 顯示照片
	public ResponseEntity<?> downloadPetPhoto(Integer id) {
		Optional<PetPhoto> optional = petPhotoRepo.findById(id);

		if (optional.isPresent()) {
			PetPhoto petPhoto = optional.get();
			byte[] photofile = petPhoto.getPetPhotoFile();
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(photofile);
		}
		return ResponseEntity.notFound().build();
	}

	// 管理員審核案件
	public void checkPetPost(Integer petCaseId, int caseStatus) {
		Optional<Adoptions> optional = adoptRepo.findById(petCaseId);
		if (optional.isPresent()) {
			Adoptions adoption = optional.get();
			adoption.setCaseStatus(caseStatus);
			adoptRepo.save(adoption);
		}
	}

	// 申請領養
	public Application applyForCase(Application application) {
		application.setApplyTime(new Date());
		application.setApplyStatus("PENDING"); // 設定初始狀態為PENDING(待送養人審核)
		Application savedApplication = applicationRepo.save(application);

		// 獲取寵物案件和發布者
		Integer petCaseId = application.getAdoptions().getPetCaseId();
		Adoptions adoption = adoptRepo.findById(petCaseId).orElse(null);

		if (adoption != null) {
			Members publisher = adoption.getMembers(); // 直接從 adoption 獲取發布者

			if (publisher != null) {
				// 發送郵件通知
				try {
					sendMail.sendApplicationRequest(publisher.getMemberEmail(), application.getApplicantName(),
							petCaseId, application.getApplyTime());
				} catch (Exception e) {
					// 處理郵件發送失敗的情況
					throw new RuntimeException("郵件發送失敗：" + e.getMessage());
				}
			}
		}
		return savedApplication;
	}

	// 取消領養
	public boolean cancelApplication(Integer applyId) {
		Optional<Application> applicationOpt = applicationRepo.findById(applyId);
		if (!applicationOpt.isPresent()) {
			return false;
		} else {
			applicationRepo.updateApplyStatus(applyId, "CANCEL"); // 更新案件狀態為「取消」
			applicationRepo.flush();
			return true;
		}
	}

	// 確認是否申請過此案件
	public boolean hasUserApplied(Integer memberId, Integer petCaseId) {
		return applicationRepo.userHasApplied(memberId, petCaseId);
	}

	// 申請時間限制（三天內）
	public List<Application> findPendingApplicationsOlderThan(Date date) {
		return applicationRepo.findOldPendingApplications(date);
	}

	@Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Taipei") // "秒 分 時 每月特定日期 月 每週特定星期"
	public void checkAndUpdateOldApplications() {
		Date threeDaysAgo = Date.from(Instant.now().minus(3, ChronoUnit.DAYS));
		List<Application> applications = findPendingApplicationsOlderThan(threeDaysAgo);

		for (Application apply : applications) {
			if ("PENDING".equals(apply.getApplyStatus())) {
				apply.setApplyStatus("EXPIRED"); // 已過期
				applicationRepo.save(apply);
			}
		}
	}

	// 取得案件申請動作的狀態
	public Map<String, Object> getApplicationStatus(Integer applyId) {
		Application application = applicationRepo.findByApplyId(applyId);

		Map<String, Object> response = new HashMap<>();
		response.put("status", application.getApplyId());

		Date applyTime = application.getApplyTime();
		Date now = new Date();
		Date threeDaysLater = Date.from(applyTime.toInstant().plus(3, ChronoUnit.DAYS));
		long remainingTime = threeDaysLater.getTime() - now.getTime();

		response.put("remainingTime", remainingTime > 0 ? remainingTime : 0);

		return response;
	}

	// 領養審核（選擇此位！）
	public List<Application> getApplicationsByPetCaseId(Integer petCaseId) {
		return applicationRepo.findByAdoptionsPetCaseId(petCaseId);
	}

	public Map<String, Object> selectApplication(Integer applyId) {
		Optional<Application> applicationOpt = applicationRepo.findById(applyId);
		if (applicationOpt.isPresent()) {
			Application application = applicationOpt.get();
			if ("SELECTED".equals(application.getApplyStatus())) {
				return null; // 已經選擇過
			}
			application.setOwnerRespond(true);
			application.setApplyStatus("SELECTED");
			applicationRepo.save(application);
			Map<String, Object> applicationInfo = new HashMap<>();
			Application applicantion = applicationRepo.findById(application.getApplyId()).orElse(null);

			if (applicantion != null) {
				applicationInfo.put("applicantEmail", applicantion.getMembers().getMemberEmail());
				applicationInfo.put("petCaseId", application.getAdoptions().getPetCaseId());
			}
			return applicationInfo;
		}
		return null;
	}

	// 後台顯示圖表
	public List<CaseStatDto> getCaseStatsByYear(int year) {
		List<CaseStatDto> stats = adoptRepo.getCaseStatsByYear(year);
		Map<String, CaseStatDto> statsMap = new HashMap<>();

		// 初始化每個月的數據
		for (int month = 1; month <= 12; month++) {
			String monthStr = String.format("%d-%02d", year, month);
			statsMap.put(monthStr, new CaseStatDto(monthStr, 0, 0, 0));
		}
		// 填入實際數據
		for (CaseStatDto stat : stats) {
			statsMap.put(stat.getMonth(), stat);
		}
		// 轉換回列表並排序
		return statsMap.values().stream().sorted(Comparator.comparing(CaseStatDto::getMonth))
				.collect(Collectors.toList());
	}

}
