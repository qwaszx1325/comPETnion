package com.pet.repository.product;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pet.model.member.Members;
import com.pet.model.product.Favorite;
import com.pet.model.product.Product;

import java.util.List;
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByMember(Members member);
    Favorite findByMemberAndProduct(Members member, Product product);
    List<Favorite> findByMemberMemberId(Integer memberId);
    List<Favorite> findByMember_MemberId(Integer memberId);
    Favorite findByMember_MemberIdAndProduct_ProductID(Integer memberId, Integer productId);
}

