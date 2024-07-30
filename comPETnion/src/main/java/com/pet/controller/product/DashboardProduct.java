package com.pet.controller.product;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.pet.model.product.Product;
import com.pet.service.product.ProductService;


@Controller
public class DashboardProduct {

	@Autowired
	private ProductService productService;
	
	private static final Map<Integer, String> productTypeMap = new HashMap<>();
    static {
        productTypeMap.put(1, "狗糧");
        productTypeMap.put(2, "貓糧");
        productTypeMap.put(3, "狗玩具");
        productTypeMap.put(4, "貓玩具");
        productTypeMap.put(5, "狗狗用品");
        productTypeMap.put(6, "貓咪用品");
    }
	
    //顯示所有商品的管理頁面
	@GetMapping("/admin/dashboard/productManagement")
	public String dashboardProductManagement(Model m) {
		List<Product> products = productService.findAllProducts();
		m.addAttribute("products",products);
		m.addAttribute("productTypeMap",productTypeMap);
		return "product/indexBackProduct";
	}
	
	
	//顯示添加新商品的表單
	@GetMapping("/admin/dashboard/addProduct")
    public String showAddProductForm(Model m) {
        m.addAttribute("product", new Product());
        m.addAttribute("productTypeMap", productTypeMap);
        return "product/addProduct";
    }

	//提交添加新商品的表單
    @PostMapping("/admin/dashboard/addProduct")
    public String addProduct(@RequestParam("productTitle") String productTitle,
                             @RequestParam("productDesc") String productDesc,
                             @RequestParam("productPrice") Integer productPrice,
                             @RequestParam("productStock") int productStock,
                             @RequestParam("productLaunchDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date productLaunchDate,
                             @RequestParam("productCondition") int productCondition,
                             @RequestParam("productType") int productType,
                             @RequestParam("productImageMain") MultipartFile productImageMain,
                             @RequestParam("productImage1") MultipartFile productImage1,
                             @RequestParam("productImage2") MultipartFile productImage2,
                             @RequestParam("productImage3") MultipartFile productImage3,
                             @RequestParam("productImage4") MultipartFile productImage4) {
        
        Product product = new Product();
        product.setProductTitle(productTitle);
        product.setProductDesc(productDesc);
        product.setProductPrice(productPrice);
        product.setProductStock(productStock);
        product.setProductLaunchDate(productLaunchDate);
        product.setProductCondition(productCondition);
        product.setProductType(productType);
        System.out.println("Main Image Size: " + productImageMain.getSize());
        try {
            productService.addProduct(product, productImageMain, productImage1, productImage2, productImage3, productImage4);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
        
        return "redirect:/admin/dashboard/productManagement";
    }
    
    @GetMapping("/admin/dashboard/couponManagement")
	public String couponManagement(Model m) {
		
		return "product/couponBack";
	}
}

