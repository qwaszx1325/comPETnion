package com.pet.service.adopt;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pet.dto.adopt.CaseStatDto;
import com.pet.model.adopt.Adoptions;
import com.pet.model.adopt.Application;
import com.pet.model.adopt.PetPhoto;
import com.pet.model.member.Members;
import com.pet.repository.adopt.AdoptRepository;
import com.pet.repository.adopt.ApplicationRepository;
import com.pet.repository.adopt.PetPhotoRepository;
import com.pet.utils.SendMail;
import com.pet.utils.ShareOnFb;

import jakarta.servlet.http.HttpServletResponse;

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
	@Autowired
	private ShareOnFb shareOnFb;

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
		Adoptions adoption = adoptRepo.findById(petCaseId)
				.orElseThrow(() -> new RuntimeException("寵物領養案件不存在: " + petCaseId));

		adoption.setCaseStatus(caseStatus);
		adoption = adoptRepo.save(adoption);
//
//		if (adoption.isAutoShareToFb() && adoption.getCaseStatus() == 1) {
//			shareOnFb.shareCase(adoption);
//		}

		try {
			if (adoption.isAutoShareToFb() && adoption.getCaseStatus() == 1) {
				System.out.println("準備分享到 FB: isAutoShareToFb=" + adoption.isAutoShareToFb() + ", caseStatus="
						+ adoption.getCaseStatus());
				shareOnFb.shareCase(adoption);
			}
		} catch (Exception e) {
			System.err.println("分享到 Facebook 時發生錯誤: " + e.getMessage());
			// 考慮是否需要將此錯誤通知給用戶或管理員
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

	// 輸出
	public List<Adoptions> getAdoptionsByYearAndMonth(Integer year, Integer month) {
		List<Adoptions> allAdoptions = adoptRepo.findAll();
		return allAdoptions.stream().filter(adoption -> {
			Calendar cal = Calendar.getInstance();
			cal.setTime(adoption.getPetPostDate());
			int adoptionYear = cal.get(Calendar.YEAR);
			int adoptionMonth = cal.get(Calendar.MONTH) + 1;
			return (year == null || year == adoptionYear) && (month == null || month == adoptionMonth);
		}).collect(Collectors.toList());
	}
	
	// 新增：導出 CSV
    public void exportToCSV(HttpServletResponse response, Integer year, Integer month) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = today.format(formatter);
        String fileName = "adoptions_" + formattedDate + ".csv";

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.write('\ufeff'); // BOM for Excel

            writer.println("會員編號,領養案件編號,寵物小名,所在縣市,區域,種類,品種,毛色,性別,體型,年齡,醫療紀錄,寵物描述,案件發布日期,目前狀態(0待審核/1已上架/2審核駁回/3已被領養)");

            List<Adoptions> adoptions = getAdoptionsByYearAndMonth(year, month);
            for (Adoptions adoption : adoptions) {
                writer.println(String.join(",", 
                    adoption.getMembers().getMemberId().toString(),
                    adoption.getPetCaseId().toString(),
                    adoption.getPetName(),
                    adoption.getPetCity(),
                    adoption.getPetDistrict(),
                    adoption.getPetKind(),
                    adoption.getPetBreed(),
                    adoption.getPetColour(),
                    adoption.getPetSex(),
                    adoption.getPetBodyType(),
                    adoption.getPetAge(),
                    adoption.getMedicalRecord(),
                    adoption.getPetDescription(),
                    adoption.getPetPostDate().toString(),
                    adoption.getCaseStatus().toString()
                ));
            }
        }
    }

    // 新增：導出 JSON
    public void exportToJSON(HttpServletResponse response, Integer year, Integer month) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = today.format(formatter);
        String fileName = "adoptions_" + formattedDate + ".json";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        List<Adoptions> adoptions = getAdoptionsByYearAndMonth(year, month);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.write('\ufeff');

            List<LinkedHashMap<String, Object>> adoptionList = new ArrayList<>();

            for (Adoptions adoption : adoptions) {
                LinkedHashMap<String, Object> adoptionMap = new LinkedHashMap<>();
                adoptionMap.put("會員編號", adoption.getMembers().getMemberId().toString());
                adoptionMap.put("領養案件編號", adoption.getPetCaseId().toString());
                adoptionMap.put("寵物小名", adoption.getPetName());
                adoptionMap.put("所在縣市", adoption.getPetCity());
                adoptionMap.put("區域", adoption.getPetDistrict());
                adoptionMap.put("種類", adoption.getPetKind());
                adoptionMap.put("品種", adoption.getPetBreed());
                adoptionMap.put("毛色", adoption.getPetColour());
                adoptionMap.put("性別", adoption.getPetSex());
                adoptionMap.put("體型", adoption.getPetBodyType());
                adoptionMap.put("年齡", adoption.getPetAge());
                adoptionMap.put("醫療紀錄", adoption.getMedicalRecord());
                adoptionMap.put("寵物描述", adoption.getPetDescription());
                adoptionMap.put("案件發布日期", adoption.getPetPostDate().toString());
                adoptionMap.put("目前狀態(0待審核/1已上架/2審核駁回/3已被領養)", adoption.getCaseStatus().toString());

                adoptionList.add(adoptionMap);
            }

            writer.println(mapper.writeValueAsString(adoptionList));
        }
    }

}
