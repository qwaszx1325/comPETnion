package com.pet.service.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.pet.model.product.Product;
import com.pet.repository.product.ProductRepository;

import java.util.Date;
import java.util.List;

@Service
public class ProductScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ProductScheduler.class);

    @Autowired
    private ProductService productService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkProductStatus() {
        logger.info("Scheduled task started");
        
        Date today = new Date();
        List<Product> products = productService.findProductsForAutoUpdate();

        for (Product product : products) {
            if (product.getProductLaunchDate().compareTo(today) <= 0) {
                product.setProductCondition(1); // 設為上架
                productService.save(product);
                logger.info("Product {} status updated to 'on-shelf'", product.getProductID());
            }
        }

        List<Product> expiredProducts = productService.findProductsForExpirationCheck();

        for (Product product : expiredProducts) {
            if (product.getProductType() == 1 || product.getProductType() == 2) { // 只處理糧食類商品
                product.setProductCondition(2); // 設為下架
                productService.save(product);
                logger.info("Product {} status updated to 'off-shelf' due to expiration", product.getProductID());
            }
        }
        
        logger.info("Scheduled task completed");
    }
    
    
}
