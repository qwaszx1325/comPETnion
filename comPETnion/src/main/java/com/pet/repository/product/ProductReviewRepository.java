package com.pet.repository.product;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pet.model.product.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
	
//	List<ProductReview> findByProductId(Integer productId);
	
	
	@Query("SELECT pr FROM ProductReview pr WHERE pr.productId = :productId ORDER BY pr.createdDate DESC")
    List<ProductReview> findByProductId(@Param("productId") Integer productId);
}