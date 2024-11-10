package com.pet.repository.adopt;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pet.model.adopt.PetPhoto;

public interface PetPhotoRepository extends JpaRepository<PetPhoto, Integer> {

}
