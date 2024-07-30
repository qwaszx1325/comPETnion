package com.pet.model.product;

import com.pet.model.member.Members;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="favorite")
public class Favorite {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer favoriteId;

    @ManyToOne
    @JoinColumn(name = "memberid")
    private Members member;

    @ManyToOne
    @JoinColumn(name = "productid")
    private Product product;
}
