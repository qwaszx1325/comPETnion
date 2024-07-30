package com.pet.service.order;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.model.order.ShoppingCart;
import com.pet.model.order.ShoppingCartView;
import com.pet.repository.order.ShoppingCartRepository;
import com.pet.repository.order.ShoppingCartViewRepository;
@Service
@Transactional
public class ShoppingCartService {
	@Autowired
	private ShoppingCartRepository ShoppingCartRepository;
	
	@Autowired
	private ShoppingCartViewRepository shoppingCartViewRepository;
	
	public void addShoppingCart(ShoppingCart shoppingCart) {
		ShoppingCartRepository.save(shoppingCart);
	}
	
	public List<ShoppingCartView> findMemberShoppingCartById(Integer memberId) {
		return shoppingCartViewRepository.findMemberShoppingCartById(memberId);
	}
	
	public boolean checkShoopingCartByMemberId(Integer memberId, Integer productId) {
		System.out.println(memberId);
		System.out.println(productId);
		ShoppingCart checkShopping = ShoppingCartRepository.checkShoopingCartByMemberId(memberId, productId);
		if(checkShopping ==null) {
			return false;
		}
		return true;
	}
	public ShoppingCart findShoopingCartByMemberId(Integer memberId, Integer productId) {
		ShoppingCart checkShopping = ShoppingCartRepository.checkShoopingCartByMemberId(memberId, productId);
		return checkShopping;
	}
	public void updateShoppingCartQuantity(ShoppingCart shoppingCart) {
		ShoppingCartRepository.save(shoppingCart);
	}
	
	public void deleteOneProduct(Integer productId, Integer memberId) {
		ShoppingCartRepository.deleteOneProduct(productId, memberId);;
	}
	
}
