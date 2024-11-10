package com.pet.repository.forum;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.forum.Theme;




public interface ThemeRepository extends JpaRepository<Theme, Integer> {

	// 模糊查詢全部
	 @Query("from Theme where themeName like %:themeName%")
	    List<Theme> findAllByThemeName(@Param(value = "themeName") String name);
	 
		// 顯示未封鎖
	  @Query("FROM Theme t WHERE t.isShow = :isShow")
	    List<Theme> findAllByIsShow(@Param("isShow") Boolean isShow);
	 
	 //是否顯示
	 List<Theme> findAllByisShow(boolean isShow);
	 
	 
	  // 修改是否封鎖
	    @Modifying
	    @Query("UPDATE Theme t SET t.isShow = :isShow WHERE t.id = :themeId")
	    void updateIsShowById(@Param("themeId") Integer themeId, @Param("isShow") boolean isShow);

       //+1遊覽次數
		Theme save(Integer postViews);
		
		//判定資料庫是否有此主題
		Theme findByThemeName(String ThemeName);
		
		 //判斷同themeId是否重複名稱
		  boolean existsByThemeNameAndThemeIdNot(String themeName, Integer themeId);
		
	 
}
