package com.pet.controller.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.pet.model.member.Members;
import com.pet.model.product.Product;
import com.pet.model.product.ProductReview;
import com.pet.service.product.ProductReviewService;
import com.pet.service.product.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
public class IndexProduct {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductReviewService productReviewService;
	
	private static final Map<Integer, String> productTypeMap = new HashMap<>();
    static {
        productTypeMap.put(1, "狗糧");
        productTypeMap.put(2, "貓糧");
        productTypeMap.put(3, "狗玩具");
        productTypeMap.put(4, "貓玩具");
        productTypeMap.put(5, "狗狗用品");
        productTypeMap.put(6, "貓咪用品");
    }
	

    //過濾掉 productCondition 不為 1（下架狀態）的商品
    @GetMapping("/public/index/productlist")
    public String productPage(@RequestParam(required = false) Integer category, Model model) {
        List<Product> products = productService.findAllProducts()
            .stream()
            .filter(product -> product.getProductCondition() == 1)  // 過濾掉下架的商品
            .filter(product -> category == null || product.getProductType().equals(category))
            .collect(Collectors.toList());

        model.addAttribute("products", products);
        model.addAttribute("productTypeMap", productTypeMap);
        return "product/indexFrontProduct";
    }
	
    //顯示單個商品詳情
    @GetMapping("/public/product/single/{id}")
    public String showProduct(@PathVariable Integer id, Model model, HttpSession session) {
        Members member = (Members) session.getAttribute("member");
        if (member != null) {
            model.addAttribute("memberId", member.getMemberId());
        }
        Optional<Product> product = productService.findProductById(id);
        if (product.isPresent()) {
        	
        	
        	List<ProductReview> reviewByProductId = productReviewService.findReviewByProductId(id);
        	double averageRating = reviewByProductId.stream()
                    .mapToInt(ProductReview::getRating)
                    .average()
                    .orElse(0.0);
                model.addAttribute("averageRating", averageRating);
        	
        	model.addAttribute("reviewByProductId",reviewByProductId);
            model.addAttribute("product", product.get());
            return "product/singleProduct";
        } else {
            return "redirect:/public/index/productlist";
        }
    }
//	@GetMapping("/product/single/{id}")
//    public String showProduct(@PathVariable Integer id, Model model) {
//        Optional<Product> product = productService.findProductById(id);
//        if (product.isPresent()) {
//            model.addAttribute("product", product.get());
//            return "product/singleProduct";
//        } else {
//            return "redirect:/index/productlist";
//        }
//    }
	
	//顯示單個商品詳情，並檢查副圖片是否存在
	@GetMapping("/public/product/details/{id}")
	public String showProductDetails(@PathVariable Integer id, Model model) {
	    Optional<Product> product = productService.findProductById(id);
	    if (product.isPresent()) {
	        Product productDetails = product.get();
	        model.addAttribute("product", productDetails);

	        // 檢查圖片是否存在
	        model.addAttribute("hasImage1", productDetails.getProductImage1() != null);
	        model.addAttribute("hasImage2", productDetails.getProductImage2() != null);
	        model.addAttribute("hasImage3", productDetails.getProductImage3() != null);
	        model.addAttribute("hasImage4", productDetails.getProductImage4() != null);

	        return "product/singleProduct";
	    } else {
	        return "redirect:/public/product/findAll";
	    }
	}

}
