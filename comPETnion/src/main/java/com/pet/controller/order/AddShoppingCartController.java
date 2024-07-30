package com.pet.controller.order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pet.model.member.Members;
import com.pet.model.order.ShoppingCart;
import com.pet.model.order.ShoppingCartView;
import com.pet.service.order.ShoppingCartService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AddShoppingCartController {

	@Autowired
	private ShoppingCartService shoppingCartService;

	//直接點進去購物車頁面
	@GetMapping("/member/shoopingCart")
	public String addShoppingCart(Model model, HttpSession session) {

		Members member = (Members) session.getAttribute("member");
			Integer memberId = member.getMemberId();
			List<ShoppingCartView> memberShoppingCartById = shoppingCartService.findMemberShoppingCartById(memberId);
			model.addAttribute("memberShoppingCartById", memberShoppingCartById);
			return "order/shoppingCart";
	}

	//加入購物車
	@SuppressWarnings("unchecked")
	@ResponseBody
	@PostMapping("/member/addShoppingCart")
	public String addShoppingCart(HttpSession session, @RequestBody ShoppingCart shoppingCart) {
		Integer productId = shoppingCart.getProductId();
		Integer quantity = shoppingCart.getQuantity();
		Members member = (Members) session.getAttribute("member");
		Integer memberId = member.getMemberId();
//		int productId = 1;
//		int quantity = 5;
			ShoppingCart addShoppingCart = new ShoppingCart();
			ArrayList<ShoppingCart> shoppingCartList = new ArrayList<>();
			ArrayList<ShoppingCart> cartSessionList = (ArrayList<ShoppingCart>) session
					.getAttribute("shoppingCartList");
			if (cartSessionList != null) {
				shoppingCartList = cartSessionList;
			}
			if (!shoppingCartService.checkShoopingCartByMemberId(memberId, productId)) {
				addShoppingCart.setMemberId(memberId);
				addShoppingCart.setProductId(productId);
				addShoppingCart.setQuantity(quantity);
				shoppingCartService.addShoppingCart(addShoppingCart);
				shoppingCartList.add(addShoppingCart);
			} else {
				ShoppingCart shoopingCartByMemberId = shoppingCartService.findShoopingCartByMemberId(memberId,
						productId);
				Integer originalQuantity = shoopingCartByMemberId.getQuantity();
				Integer newQuantity = originalQuantity + quantity;
				shoopingCartByMemberId.setQuantity(newQuantity);
				shoppingCartService.updateShoppingCartQuantity(shoopingCartByMemberId);

			}
			List<ShoppingCartView> memberShoppingCart = shoppingCartService.findMemberShoppingCartById(memberId);
			DecimalFormat df = new DecimalFormat("00");
			String shoppingCartQuantity = df.format(memberShoppingCart.size()); 
			return shoppingCartQuantity;

	}
	//ajax直接抓購物車欄位數量
	@GetMapping("/member/checkShoppingCartQuantity")
	@ResponseBody
	public ResponseEntity<?> checkShoppingCartQuantity(HttpSession session) {
	    Members member = (Members) session.getAttribute("member");
	    if (member != null) {
	        Integer memberId = member.getMemberId();
	        List<ShoppingCartView> memberShoppingCartById = shoppingCartService.findMemberShoppingCartById(memberId);
	        DecimalFormat df = new DecimalFormat("00");
	        String shoppingCartQuantity = df.format(memberShoppingCartById.size());
	        return ResponseEntity.ok(shoppingCartQuantity);
	    }
	    // 如果用戶未登入，返回 401 Unauthorized 狀態碼
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
	}

	// 購買按鍵
	@PostMapping("/member/shoopingCart")
	public String addShoppingCart(Model model, HttpSession session, @RequestParam("productId") Integer productId,
			@RequestParam("quantity") Integer quantity) {

		Members member = (Members) session.getAttribute("member");
			Integer memberId = member.getMemberId();
//		int productId = 1;
//		int quantity = 5;
			ShoppingCart shoppingCart = new ShoppingCart();

			if (!shoppingCartService.checkShoopingCartByMemberId(memberId, productId)) {
				shoppingCart.setMemberId(memberId);
				shoppingCart.setProductId(productId);
				shoppingCart.setQuantity(quantity);
				shoppingCartService.addShoppingCart(shoppingCart);
			}
			List<ShoppingCartView> memberShoppingCartById = shoppingCartService.findMemberShoppingCartById(memberId);
			session.setAttribute("shoppingCartList", memberShoppingCartById);
			model.addAttribute("memberShoppingCartById", memberShoppingCartById);

			return "order/shoppingCart";
	}

	//還沒做好
	@ResponseBody
	@GetMapping("/member/removeItem")
	public String removeItem(@RequestParam("productId") Integer productId, HttpSession session) {
		Members member = (Members) session.getAttribute("member");
		if (member != null) {
			Integer memberId = member.getMemberId();
//		Integer memberId = 1005;
			shoppingCartService.deleteOneProduct(productId, memberId);

			return "刪除成功";
		} else {
			return "刪除失敗";
		}
	}

}
