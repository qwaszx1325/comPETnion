package com.pet.service.forum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pet.model.forum.Forumphotos;
import com.pet.model.forum.Post;
import com.pet.repository.forum.PhotosRepository;

@Service
public class PhotoService {
	
	@Autowired
	private PhotosRepository photosRepo;
	
	  private final Path fileStorageLocation = null;

	 
	  
	  

    public String storeFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation);
        return "\\src\\main\\resources\\static\\imgs" + fileName;
    }
	
	
	
	//尋找單前PostId圖片
	 public Forumphotos getPhotoDataByPostId(Integer postId) {
	        List<Forumphotos> photos = photosRepo.findByPostPostId(postId);
	        if (!photos.isEmpty()) {
	            return photos.get(0); // 返回第一个匹配的图片对象
	        } else {
	            return null; // 或者根据业务逻辑返回适当的默认值或抛出异常
	        }
	    }
	
	
	public Forumphotos insertPhoto(Forumphotos photos) {
		
		
		return photosRepo.save(photos);
	}
	
	public Forumphotos findPhotosById(Integer id) {
		Optional<Forumphotos> optional = photosRepo.findById(id);
		
		if(optional.isPresent()) {
			return optional.get();
		}
		
		return null;
	}
	
	public List<Forumphotos> findAllPhotos(){
		return photosRepo.findAll();
	}


}
