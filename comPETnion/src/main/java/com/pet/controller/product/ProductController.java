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
import com.pet.model.product.Product;
import com.pet.service.product.FavoriteService;
import com.pet.service.product.ProductService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
    private FavoriteService favoriteService;
	
	private static final Map<Integer, String> productTypeMap = new HashMap<>();
    static {
        productTypeMap.put(1, "狗糧");
        productTypeMap.put(2, "貓糧");
        productTypeMap.put(3, "狗玩具");
        productTypeMap.put(4, "貓玩具");
        productTypeMap.put(5, "狗狗用品");
        productTypeMap.put(6, "貓咪用品");
    }

 // 搜尋全部商品
    @GetMapping("/admin/product/findAll")
    public String findAllProducts(Model model) {
        List<Product> products = productService.findAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("productTypeMap", productTypeMap);
        return "product/indexBackProduct"; // 返回顯示全部商品的視圖名稱
    }

	// 顯示新增商品的表單
	@GetMapping("/admin/product/add")
    public String showAddProductForm() {
        return "product/addProduct"; // 返回對應HTML表單的路徑
    }

	// 新增商品
	@PostMapping("/admin/product/add")
	public String addProduct(
	        @RequestParam("productCondition") Integer productCondition,
	        @RequestParam("productTitle") String productTitle,
	        @RequestParam("productDesc") String productDesc,
	        @RequestParam("productPrice") Integer productPrice,
	        @RequestParam("productStock") Integer productStock,
	        @RequestParam("productLaunchDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date productLaunchDate,
	        //@RequestParam("productSold") Integer productSold,
	        @RequestParam("productImageMain") MultipartFile productImageMain,
	        @RequestParam("productImage1") MultipartFile productImage1,
	        @RequestParam("productImage2") MultipartFile productImage2,
	        @RequestParam("productImage3") MultipartFile productImage3,
	        @RequestParam("productImage4") MultipartFile productImage4,
	        @RequestParam("productType") Integer productTypeID,
	        @RequestParam(value = "expirationDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date expirationDate,
	        RedirectAttributes redirectAttributes) throws IOException {

	    Product product = new Product();
	    product.setProductCondition(productCondition);
	    product.setProductTitle(productTitle);
	    product.setProductDesc(productDesc);
	    product.setProductPrice(productPrice);
	    product.setProductStock(productStock);
	    product.setProductLaunchDate(productLaunchDate);
	    //product.setProductSold(productSold);
	    product.setProductType(productTypeID);
	    if ((productTypeID == 1 || productTypeID == 2) && expirationDate != null) {
	        product.setExpirationDate(expirationDate);
	    } else {
	        product.setExpirationDate(null);
	    }

	    // 設置圖片字節數組
	    if (productImageMain != null && !productImageMain.isEmpty()) {
	        product.setProductImageMain(productImageMain.getBytes());
	        System.out.println("Main Image Size: " + productImageMain.getBytes().length);
	    }
	    if (productImage1 != null && !productImage1.isEmpty()) {
	        product.setProductImage1(productImage1.getBytes());
	        System.out.println("Image 1 Size: " + productImage1.getBytes().length);
	    }
	    if (productImage2 != null && !productImage2.isEmpty()) {
	        product.setProductImage2(productImage2.getBytes());
	        System.out.println("Image 2 Size: " + productImage2.getBytes().length);
	    }
	    if (productImage3 != null && !productImage3.isEmpty()) {
	        product.setProductImage3(productImage3.getBytes());
	        System.out.println("Image 3 Size: " + productImage3.getBytes().length);
	    }
	    if (productImage4 != null && !productImage4.isEmpty()) {
	        product.setProductImage4(productImage4.getBytes());
	        System.out.println("Image 4 Size: " + productImage4.getBytes().length);
	    }

	    productService.addProduct(product, productImageMain, productImage1, productImage2, productImage3, productImage4);
	    redirectAttributes.addFlashAttribute("message", "商品新增成功！");
	    return "redirect:/admin/product/findAll";
	}



	// 顯示修改商品的表單
	@GetMapping("/admin/product/update/{id}")
	public String showEditProductForm(@PathVariable Integer id, Model model) {
		Optional<Product> product = productService.findProductById(id);
		if (product.isPresent()) {
			model.addAttribute("product", product.get());
			model.addAttribute("productTypes", productTypeMap);
			
			// 檢查並添加圖片存在的標記
	        model.addAttribute("hasImageMain", product.get().getProductImageMain() != null);
	        model.addAttribute("hasImage1", product.get().getProductImage1() != null);
	        model.addAttribute("hasImage2", product.get().getProductImage2() != null);
	        model.addAttribute("hasImage3", product.get().getProductImage3() != null);
	        model.addAttribute("hasImage4", product.get().getProductImage4() != null);
			
			return "/product/updateProduct";
		} else {
			return "redirect:/admin/product/findAll";
		}
	}

	// 修改商品
	@PostMapping("/admin/product/update/{id}")
	public String updateProduct(@PathVariable Integer id, @RequestParam("productCondition") Integer productCondition,
			@RequestParam("productTitle") String productTitle, @RequestParam("productDesc") String productDesc,
			@RequestParam("productPrice") Integer productPrice, @RequestParam("productStock") Integer productStock,
			@RequestParam("productLaunchDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date productLaunchDate,
			//@RequestParam("productSold") Integer productSold,
			@RequestParam("productImageMain") MultipartFile productImageMain,
			@RequestParam("productImage1") MultipartFile productImage1,
			@RequestParam("productImage2") MultipartFile productImage2,
			@RequestParam("productImage3") MultipartFile productImage3,
			@RequestParam("productImage4") MultipartFile productImage4,
			@RequestParam("productType") Integer productTypeID,
			@RequestParam(value = "expirationDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date expirationDate,
			@RequestParam(value = "deleteImageMain", required = false) boolean deleteImageMain,
            @RequestParam(value = "deleteImage1", required = false) boolean deleteImage1,
            @RequestParam(value = "deleteImage2", required = false) boolean deleteImage2,
            @RequestParam(value = "deleteImage3", required = false) boolean deleteImage3,
            @RequestParam(value = "deleteImage4", required = false) boolean deleteImage4,
            RedirectAttributes redirectAttributes) throws IOException {

		Optional<Product> optionalProduct = productService.findProductById(id);
		if (optionalProduct.isPresent()) {
			Product product = optionalProduct.get();
			product.setProductCondition(productCondition); // 設置商品狀態
			product.setProductTitle(productTitle); // 設置商品名稱
			product.setProductDesc(productDesc); // 設置商品描述
			product.setProductPrice(productPrice); // 設置商品價格
			product.setProductStock(productStock); // 設置商品庫存
			product.setProductLaunchDate(productLaunchDate); // 設置商品上架日期
			//product.setProductSold(productSold); // 設置已售數量
			product.setProductType(productTypeID); // 設置商品類型
			product.setExpirationDate(expirationDate); // 設置到期日
			
			// 設置圖片字節數組
            if (deleteImageMain) {
                product.setProductImageMain(null);
            } else if (productImageMain != null && !productImageMain.isEmpty()) {
                product.setProductImageMain(productImageMain.getBytes());
            }

            if (deleteImage1) {
                product.setProductImage1(null);
            } else if (productImage1 != null && !productImage1.isEmpty()) {
                product.setProductImage1(productImage1.getBytes());
            }

            if (deleteImage2) {
                product.setProductImage2(null);
            } else if (productImage2 != null && !productImage2.isEmpty()) {
                product.setProductImage2(productImage2.getBytes());
            }

            if (deleteImage3) {
                product.setProductImage3(null);
            } else if (productImage3 != null && !productImage3.isEmpty()) {
                product.setProductImage3(productImage3.getBytes());
            }

            if (deleteImage4) {
                product.setProductImage4(null);
            } else if (productImage4 != null && !productImage4.isEmpty()) {
                product.setProductImage4(productImage4.getBytes());
            }


			productService.updateProduct(product, productImageMain, productImage1, productImage2, productImage3, productImage4);
            redirectAttributes.addFlashAttribute("message", "商品更新成功！");
            return "redirect:/admin/product/findAll";
        } else {
            redirectAttributes.addFlashAttribute("error", "商品未找到！");
            return "redirect:/admin/product/findAll";
        }
	}

	// 刪除商品
	@GetMapping("/admin/product/delete/{id}")
	public String deleteProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		Optional<Product> product = productService.findProductById(id);
		if (product.isPresent()) {
			productService.deleteProduct(id);
			redirectAttributes.addFlashAttribute("message", "商品刪除成功！");
		} else {
			redirectAttributes.addFlashAttribute("error", "商品未找到！");
		}
		return "redirect:/admin/product/findAll";
	}
	
	// 顯示圖片
	@GetMapping("/product/images")
	@ResponseBody
	public ResponseEntity<byte[]> showProductImage(@RequestParam("productId") Integer productId, @RequestParam("type") String type) {
	    Optional<Product> optionalProduct = productService.findProductById(productId);
	    if (optionalProduct.isPresent()) {
	        Product product = optionalProduct.get();
	        byte[] imageBytes = null;
	        switch (type) {
	            case "main":
	                imageBytes = product.getProductImageMain();
	                break;
	            case "image1":
	                imageBytes = product.getProductImage1();
	                break;
	            case "image2":
	                imageBytes = product.getProductImage2();
	                break;
	            case "image3":
	                imageBytes = product.getProductImage3();
	                break;
	            case "image4":
	                imageBytes = product.getProductImage4();
	                break;
	        }
	        if (imageBytes != null) {
	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.IMAGE_JPEG);
	            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	        }
	    }
	    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/admin/updateProductCondition")
	public ResponseEntity<String> updateProductCondition(@RequestParam Integer productId, @RequestParam Integer condition) {
	    productService.manualSetProductCondition(productId, condition);
	    return ResponseEntity.ok("Product condition updated successfully");
	}
	
	@GetMapping("/public/productlist")
    public String productList(@RequestParam(value = "productType", required = false) Integer productType, Model model) {
        List<Product> products;
        if (productType != null) {
            products = productService.getProductsByCategory(productType);
        } else {
            products = productService.findByProductCondition();
        }
        model.addAttribute("products", products);
        model.addAttribute("productTypeMap", productTypeMap);
        return "product/indexFrontProduct";
    }

}