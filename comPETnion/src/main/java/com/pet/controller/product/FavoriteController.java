package com.pet.controller.product;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.pet.model.member.Members;
import com.pet.model.product.Favorite;
import com.pet.service.product.FavoriteService;

import jakarta.servlet.http.HttpSession;

@Controller
public class FavoriteController {
	
    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/member/addFavorite")
    public ResponseEntity<String> addFavorite(@RequestBody Map<String, Integer> payload, HttpSession session) {
        Integer productId = payload.get("productId");
        Members member = (Members) session.getAttribute("member");
        
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Member not logged in");
        }

        favoriteService.addFavorite(productId, member.getMemberId());
        return ResponseEntity.ok("Favorite added successfully");
    }

    @DeleteMapping("/member/removeFavorite")
    public ResponseEntity<String> removeFavorite(@RequestParam("productId") Integer productId, HttpSession session) {
        Members member = (Members) session.getAttribute("member");

        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Member not logged in");
        }

        Integer memberId = member.getMemberId();
        System.out.println("Trying to remove favorite for memberId: " + memberId + " and productId: " + productId);

        boolean success = favoriteService.removeFavorite(memberId, productId);
        if (success) {
            System.out.println("Favorite removed successfully");
            return ResponseEntity.ok("移除最愛成功");
        } else {
            System.out.println("Failed to remove favorite");
            return ResponseEntity.status(404).body("未找到匹配的最愛商品或會員不存在");
        }
    }
    
    @GetMapping("/favorites")
    public String getFavoritesByMember(HttpSession session, Model model) {
        Members member = (Members) session.getAttribute("member");
        if (member == null) {
            return "redirect:/members/login";
        }
        List<Favorite> favorites = favoriteService.getFavoritesByMemberId(member.getMemberId());
        model.addAttribute("favorites", favorites);
        return "/product/favoriteProduct";
    }
}
