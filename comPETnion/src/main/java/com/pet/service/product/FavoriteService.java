package com.pet.service.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.model.member.Members;
import com.pet.model.product.Favorite;
import com.pet.model.product.Product;
import com.pet.repository.member.MembersRepository;
import com.pet.repository.product.FavoriteRepository;
import com.pet.repository.product.ProductRepository;

import java.util.List;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MembersRepository membersRepository;

    @Autowired
    private ProductRepository productRepository;

    public boolean addFavorite(Integer productId,Integer memberId) {
    	Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId));
            Members member = membersRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member Id:" + memberId));

        if (member == null || product == null) {
            System.out.println("會員或商品不存在");
            return false; // 會員或商品不存在
        }

        Favorite existingFavorite = favoriteRepository.findByMemberAndProduct(member, product);
        if (existingFavorite != null) {
            System.out.println("商品已在最愛清單中");
            return false; // 商品已在最愛清單中
        }

        Favorite favorite = new Favorite();
        favorite.setMember(member);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
        System.out.println("成功加入最愛");
        return true;
    }

    public List<Favorite> getFavoritesByMemberId(Integer memberId) {
        return favoriteRepository.findByMemberMemberId(memberId);
    }

    public boolean removeFavorite(Integer memberId, Integer productId) {
        Members member = membersRepository.findById(memberId).orElse(null);
        Product product = productRepository.findById(productId).orElse(null);

        if (member == null || product == null) {
            System.out.println("會員或商品不存在");
            return false; // 會員或商品不存在
        }

        Favorite favorite = favoriteRepository.findByMemberAndProduct(member, product);
        if (favorite != null) {
            favoriteRepository.delete(favorite);
            System.out.println("成功刪除最愛");
            return true;
        }

        System.out.println("未找到匹配的最愛商品");
        return false; // 未找到匹配的最愛商品
    }
}
