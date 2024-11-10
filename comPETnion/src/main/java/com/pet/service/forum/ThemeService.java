package com.pet.service.forum;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pet.model.adopt.Adoptions;
import com.pet.model.event.Event;
import com.pet.model.forum.Post;
import com.pet.model.forum.Theme;
import com.pet.repository.forum.ThemeRepository;



@Service
public class ThemeService {

	// 注入
	@Autowired
	private ThemeRepository themeRepository;
	
	

	
	
	//確定是否有重複主題
	public boolean checkThemNameExist(String themeName) {
	    List<Theme> allByThemeName = themeRepository.findAllByThemeName(themeName);
	    return !allByThemeName.isEmpty();
	}
	public boolean checkThemeNameExistExcludingCurrent(String themeName, Integer currentThemeId) {
	    // 實現此方法以檢查除了當前主題外是否存在同名主題
	    // 例如：
	    return themeRepository.existsByThemeNameAndThemeIdNot(themeName, currentThemeId);
	}
	
	// 新增
	public Theme insertViews(Integer themeId) {
	    Optional<Theme> optional = themeRepository.findById(themeId);
	    
	    if (optional.isPresent()) {
	        Theme theme = optional.get();
	        
	        
	        Integer currentViews = theme.getThemeViews();
	        if (currentViews == null) {
	            currentViews = 0; // 如果為 null，設置為 0
	        }
	        theme.setThemeViews(currentViews + 1); // 遊覽次數加 1
	        return themeRepository.save(theme);
	    } else {
	        return null;
	    }
	}

	
	
	
	
	//修改狀態
	public Theme updateShowStatus(Integer themeId, Boolean isShow) {
	    Optional<Theme> optionalTheme = themeRepository.findById(themeId);
	    if (optionalTheme.isPresent()) {
	        Theme theme = optionalTheme.get();
	        theme.setIsShow(isShow);
	        System.out.println(isShow);
	        return themeRepository.save(theme);
	    } else {
	        return null; // or handle the case where Theme is not found
	    }
	}
	
	
	//分頁 查詢 當themeId的前12筆最多的遊覽次數
	 public List<Theme> findThemeViewsByPage(boolean isShow) {
	        Sort.by(Sort.Direction.DESC, "themeViews");
	        return themeRepository.findAllByisShow(isShow);
	    }

	

	// id查詢
	public Theme findById(Integer id) {
		Optional<Theme> optional = themeRepository.findById(id);

		if (optional.isEmpty()) {
			return null;
		}

		return optional.get();
	}

	// 模糊themeName查詢
	public List<Theme> findTheme(String themeName) {
		return themeRepository.findAllByThemeName(themeName);
	}

	// 查詢全部
	public List<Theme> findAll() {
		return themeRepository.findAllByisShow(true);
	}
	// 查詢全部
	public List<Theme> findAllback() {
		return themeRepository.findAll();
	}
	
	// 查詢狀態為顯示
		public List<Theme> findAllByIsShow() {
			return themeRepository.findAllByisShow(true);
		}


	// 新增
		  public Theme insert(MultipartFile forumPhoto, String themeName,  String themeDescription) throws IOException {
		     System.out.println(123123123);
			  
			  Theme theme = new Theme();
		        theme.setIsShow(true);
		        theme.setForumPhoto(forumPhoto.getBytes());
		        theme.setThemeName(themeName);
		        theme.setThemeDescription(themeDescription);
		        return themeRepository.save(theme);
		    }

	// 修改主题
		  @Transactional
		  public Theme update(Integer themeId, MultipartFile forumPhoto, String themeName,  String themeDescription) throws IOException {
		      if (themeRepository.existsById(themeId)) {
		          Theme existingTheme = themeRepository.findById(themeId).orElse(null);

		          if (existingTheme != null) {
		              existingTheme.setForumPhoto(forumPhoto.getBytes());
		              existingTheme.setThemeName(themeName);
		              existingTheme.setThemeDescription(themeDescription);
		              return themeRepository.save(existingTheme);
		          } else {
		              System.out.println("查無主題");
		              return null;
		          }
		      } else {
		          System.out.println("查無主題");
		          return null;
		      }
		  }


	// 删除主题
	@Transactional
	public void deleteById(Integer id) {
	    if (themeRepository.existsById(id)) {
	        themeRepository.deleteById(id);
	    } else {
	    	System.out.println("查無主題");
	    }
	}

}
