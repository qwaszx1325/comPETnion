package com.pet.service.product;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pet.model.product.Product;
import com.pet.repository.product.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository prodRepo;

	public List<Product> findAllProducts() {
		return prodRepo.findAll();
	}

	public Optional<Product> findProductById(Integer id) {
		return prodRepo.findById(id);
	}

	public Product addProduct(Product product, MultipartFile productImageMain, MultipartFile productImage1,
			MultipartFile productImage2, MultipartFile productImage3, MultipartFile productImage4) throws IOException {

		if (productImageMain != null && !productImageMain.isEmpty()) {
			product.setProductImageMain(productImageMain.getBytes());
		}
		if (productImage1 != null && !productImage1.isEmpty()) {
			product.setProductImage1(productImage1.getBytes());
		}
		if (productImage2 != null && !productImage2.isEmpty()) {
			product.setProductImage2(productImage2.getBytes());
		}
		if (productImage3 != null && !productImage3.isEmpty()) {
			product.setProductImage3(productImage3.getBytes());
		}
		if (productImage4 != null && !productImage4.isEmpty()) {
			product.setProductImage4(productImage4.getBytes());
		}

		product.setProductAddDate(new Date()); // 設置商品新增日期

		return prodRepo.save(product);
	}

	public Product updateProduct(Product product, MultipartFile productImageMain, MultipartFile productImage1,
			MultipartFile productImage2, MultipartFile productImage3, MultipartFile productImage4) throws IOException {

		// 打印消息
		System.out.println("Updating Product Images...");
		if (productImageMain != null && !productImageMain.isEmpty()) {
			System.out.println("Main Image Size: " + productImageMain.getSize());
			product.setProductImageMain(productImageMain.getBytes());
		}
		if (productImage1 != null && !productImage1.isEmpty()) {
			System.out.println("Image 1 Size: " + productImage1.getSize());
			product.setProductImage1(productImage1.getBytes());
		}
		if (productImage2 != null && !productImage2.isEmpty()) {
			System.out.println("Image 2 Size: " + productImage2.getSize());
			product.setProductImage2(productImage2.getBytes());
		}
		if (productImage3 != null && !productImage3.isEmpty()) {
			System.out.println("Image 3 Size: " + productImage3.getSize());
			product.setProductImage3(productImage3.getBytes());
		}
		if (productImage4 != null && !productImage4.isEmpty()) {
			System.out.println("Image 4 Size: " + productImage4.getSize());
			product.setProductImage4(productImage4.getBytes());
		}

		product.setProductAddDate(new Date()); // 更新商品新增日期

		return prodRepo.save(product);
	}

	public void deleteProduct(Integer id) {
		prodRepo.deleteById(id);
	}
	
	public List<Product> findAll() {
        return prodRepo.findAll();
    }
	public List<Product> findByProductCondition(){
		 return prodRepo.findByProductCondition(1);
	}
	public List<Product> getProductsByCategory(Integer productType) {
        return prodRepo.findByProductTypeAndProductCondition(productType, 1);
    }

    public Product save(Product product) {
        return prodRepo.save(product);
    }
    
    public List<Product> findProductsForAutoUpdate() {
        return prodRepo.findByProductConditionAndManualOverride(2, false);
    }
    
    public void manualSetProductCondition(Integer productId, Integer condition) {
        Product product = prodRepo.findById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId));
        product.setProductCondition(condition);
        if (condition == 2) { // 如果是手動下架
            product.setManualOverride(true);
        } else { // 如果不是手動下架，重置 manualOverride
            product.setManualOverride(false);
        }
        prodRepo.save(product);
    }
    
    public List<Product> findProductsForExpirationCheck() {
        Date today = new Date();
        return prodRepo.findByProductConditionAndManualOverrideAndExpirationDateBefore(1, false, today);
    }
}