package com.pet.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import com.pet.model.product.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	List<Product> findByProductConditionAndManualOverride(Integer productCondition, Boolean manualOverride);
	List<Product> findByProductConditionAndManualOverrideAndExpirationDateBefore(int productCondition, boolean manualOverride, Date today);
	List<Product> findByProductTypeAndProductCondition(Integer productType , Integer productCondition);
	List<Product> findByProductCondition(Integer productCondition);
}

