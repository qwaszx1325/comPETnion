package com.pet.service.adopt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pet.dto.adopt.AdoptDto;
import com.pet.model.adopt.Adoptions;
import com.pet.model.adopt.Application;
import com.pet.model.adopt.PetPhoto;
import com.pet.model.member.Members;
import com.pet.repository.adopt.AdoptRepository;
import com.pet.repository.adopt.ApplicationRepository;
import com.pet.repository.adopt.PetPhotoRepository;
import com.pet.repository.member.MembersRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AdoptCRUDService {

	@Autowired
	private AdoptRepository adoptRepo;
	@Autowired
	private ApplicationRepository applicationRepo;
	@Autowired
	private PetPhotoRepository petPhotoRepo;
	@Autowired
	private MembersRepository membersRepo;

	// 會員發布案件（前台）
	public Adoptions createPetCase(AdoptDto adoptDto) {
		Adoptions adoption = new Adoptions();

		Members member = membersRepo.findById(adoptDto.getMemberId()).orElseThrow();
		adoption.setMembers(member);

		adoption.setPetName(adoptDto.getPetName());
		adoption.setPetCity(adoptDto.getPetCity());
		adoption.setPetDistrict(adoptDto.getPetDistrict());
		adoption.setPetKind(adoptDto.getPetKind());
		adoption.setPetBreed(adoptDto.getPetBreed());
		adoption.setPetColour(adoptDto.getPetColour());
		adoption.setPetSex(adoptDto.getPetSex());
		adoption.setPetBodyType(adoptDto.getPetBodyType());
		adoption.setPetAge(adoptDto.getPetAge());
		adoption.setMedicalRecord(adoptDto.getMedicalRecord());
		adoption.setPetDescription(adoptDto.getPetDescription());
		adoption.setPetPostDate(adoptDto.getPetPostDate());
		adoption.setCaseStatus(0); // 待管理員審核
		adoption.setAutoShareToFb(adoptDto.isAutoShareToFb()); // 會員決定是否自動分享

		// 上傳多張照片
		List<PetPhoto> petPhotoList = new ArrayList<>();
		MultipartFile[] files = adoptDto.getPetPhotoFile();
		if (files != null && files.length != 0) {
			for (MultipartFile oneFile : files) {
				try {
					PetPhoto petPhoto = new PetPhoto();
					petPhoto.setPetPhotoFile(oneFile.getBytes());
					petPhoto.setAdoptions(adoption);
					petPhotoList.add(petPhoto);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		adoption.setPetPhoto(petPhotoList);
		adoptRepo.save(adoption);
		adoptRepo.flush();
		return adoption;
	}

	// 假刪除-狀態更改為已被領養（前台）
	public boolean changeToAdoptedById(Integer petCaseId) {
		Optional<Adoptions> adoptionOpt = adoptRepo.findById(petCaseId);
		if (!adoptionOpt.isPresent()) {
			return false;
		} else {
			adoptRepo.updateCaseStatus(petCaseId, 3); // 更新案件狀態為「已被領養(3)」
			adoptRepo.flush();
			return true;
		}
	}

	// 取得案件資訊（for更新、前台+後台查看）
	public Adoptions getDetailByPetCaseId(Integer petCaseId) {
		Optional<Adoptions> optional = adoptRepo.findById(petCaseId);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	// 更新領養案件（後台）
	public void updatePetCase(AdoptDto adoptDto) {
		Adoptions adoption = adoptRepo.findById(adoptDto.getPetCaseId()).orElse(null);

		if (adoption != null) {
			Members member = membersRepo.findById(adoptDto.getMemberId()).orElseThrow();
			adoption.setMembers(member);

			adoption.setPetCaseId(adoptDto.getPetCaseId());
			adoption.setPetName(adoptDto.getPetName());
			adoption.setPetCity(adoptDto.getPetCity());
			adoption.setPetDistrict(adoptDto.getPetDistrict());
			adoption.setPetKind(adoptDto.getPetKind());
			adoption.setPetBreed(adoptDto.getPetBreed());
			adoption.setPetColour(adoptDto.getPetColour());
			adoption.setPetSex(adoptDto.getPetSex());
			adoption.setPetAge(adoptDto.getPetAge());
			adoption.setPetBodyType(adoptDto.getPetBodyType());
			adoption.setMedicalRecord(adoptDto.getMedicalRecord());
			adoption.setPetDescription(adoptDto.getPetDescription());
			adoption.setCaseStatus(0); // 更新後重新審核

			// 刪除指定的照片
			List<Integer> deletePhotoIds = adoptDto.getDeletePhotoIds();
			if (deletePhotoIds != null && !deletePhotoIds.isEmpty()) {
				for (Integer photoId : deletePhotoIds) {
					petPhotoRepo.deleteById(photoId);
				}
			}

			MultipartFile[] files = adoptDto.getPetPhotoFile();
			if (files != null && files.length > 0) {
				for (MultipartFile file : files) {
					if (file != null && !file.isEmpty()) { // 確認文件不是空的
						try {
							PetPhoto petPhoto = new PetPhoto();
							petPhoto.setPetPhotoFile(file.getBytes());
							petPhoto.setAdoptions(adoption);
							adoption.getPetPhoto().add(petPhoto);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			adoptRepo.save(adoption);
		}
	}

	// 查看自己發布之案件（後台）
	public List<Adoptions> findByMemberId(Integer memberId) {
		List<Adoptions> adoptions = adoptRepo.findByMemberId(memberId);
		return adoptions;
	}

	// 查看自己申請領養之案件（後台）
	public List<Application> findMyApplication(Integer memberId) {
		List<Application> applications = applicationRepo.findByMembersMemberId(memberId);
		return applications;
	}

	// 查看所有案件（管理員）
	public List<Adoptions> getAllAdoptions() {
		List<Adoptions> adoptions = adoptRepo.findAll(Sort.by(Sort.Direction.DESC, "petPostDate"));
		return adoptions;
	}

	// 複雜查詢-藉由區域、種類、體型查詢案件（前台）
	// root:取得 model 指定型態，builder: 邏輯處理，query: 組合語句
	public List<Adoptions> searchAdoptions(String petCity, String petKind, String petBodyType) {
		Specification<Adoptions> spec = (Root<Adoptions> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (petCity != null && !petCity.isEmpty()) {
				predicates.add(builder.like(root.get("petCity"), "%" + petCity + "%")); // 模糊查詢
			}

			if (petKind != null && !petKind.isEmpty()) {
				predicates.add(builder.like(root.get("petKind"), "%" + petKind + "%"));
			}

			if (petBodyType != null && !petBodyType.isEmpty()) {
				predicates.add(builder.like(root.get("petBodyType"), "%" + petBodyType + "%"));
			}
			query.orderBy(builder.desc(root.get("petPostDate")));

			return builder.and(predicates.toArray(new Predicate[0]));
		};
		return adoptRepo.findAll(spec);
	}

}
