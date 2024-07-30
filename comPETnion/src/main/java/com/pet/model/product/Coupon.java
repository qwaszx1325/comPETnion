package com.pet.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Coupon")
@Getter
@Setter
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Double discount;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expirationDate;
}

