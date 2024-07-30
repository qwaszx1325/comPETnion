package com.pet.repository.forum;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.forum.Forumphotos;


public interface PhotosRepository extends JpaRepository<Forumphotos, Integer> {
 
	 List<Forumphotos> findByPostPostId(Integer postId);
}
