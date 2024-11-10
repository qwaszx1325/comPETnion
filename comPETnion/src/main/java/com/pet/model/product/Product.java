package com.pet.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 設置主鍵生成自動增長
    private Integer productID; // 商品編號

    private Integer productType; // 商品類型
    
    private Integer productCondition; // 商品狀態 (1 代表上架, 2 代表下架)

    private String productTitle; // 商品名稱

    @Column(length = 4000)
    private String productDesc; // 商品描述

    private Integer productPrice; // 商品價格

    private Integer productStock; // 商品庫存

    @Temporal(TemporalType.DATE) // 指定日期格式
    private Date productLaunchDate; // 商品上架日期

    //private Integer productSold; // 已售數量

    @Lob
    private byte[] productImageMain; // 商品主圖

    @Lob
    private byte[] productImage1; // 商品副圖1

    @Lob
    private byte[] productImage2; // 商品副圖2

    @Lob
    private byte[] productImage3; // 商品副圖3

    @Lob
    private byte[] productImage4; // 商品副圖4
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date productAddDate; // 商品新增日期 (新增或修改時更新)
    
    @Temporal(TemporalType.DATE)
    private Date expirationDate; // 新增的欄位：商品到期日
    
    private Boolean manualOverride = false;; // true 表示手動調整，false 表示自動控制
}