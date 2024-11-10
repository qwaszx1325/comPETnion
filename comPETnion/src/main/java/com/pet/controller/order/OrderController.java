package com.pet.controller.order;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pet.dto.order.OrderDetailDto;
import com.pet.dto.order.OrderViewByDateDto;
import com.pet.model.member.Members;
import com.pet.model.order.MonthlyOrderTotalsWithMembers;
import com.pet.model.order.OrderDetailView;
import com.pet.model.order.OrderView;
import com.pet.model.order.Orders;
import com.pet.model.product.Coupon;
import com.pet.repository.order.OrderDetailViewRepository;
import com.pet.service.order.OrderDetailService;
import com.pet.service.order.OrderService;
import com.pet.service.product.CouponService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {

	@Autowired
	private OrderDetailService orderDetailService;

	@Autowired
	private OrderService orderService;
	
	 @Autowired
	 private CouponService couponService;
	 
	 

	@GetMapping("/member/OrderDetailList")
	public String orderDetailList(@RequestParam("orderId") String orderId, Model m, HttpSession session) {
		Members member = (Members) session.getAttribute("member");
			Integer memberId = member.getMemberId();
			// 可以做判斷訂單是否為這位會員的才能看到(懶得做)
			List<OrderDetailView> orderDetails = orderDetailService.findOrderDetail(orderId);
			Orders order = orderDetailService.findRecipientInfoByOrderId(orderId);
			m.addAttribute("order", order);
			m.addAttribute("orderDetails", orderDetails);
			return "order/orderDetailList";
	}

	// 結帳跳到付款畫面(填寫收件人資訊)
	@PostMapping("/member/checkout")
	public String checkout(@RequestParam("items") String items, @RequestParam("totalPrice") String totalPrice,
			HttpSession session, Model m, RedirectAttributes redirectAttributes) {
		Members member = (Members) session.getAttribute("member");
			Integer memberId = member.getMemberId();

			String orderId = orderService.checkout(items, totalPrice, memberId);
			
			List<OrderDetailView> orderDetails = orderDetailService.findOrderDetail(orderId);
			m.addAttribute("orderDetails", orderDetails);
			redirectAttributes.addFlashAttribute("orderId", orderId);
			// 未更改
			return "order/recipientInfoPage";
	}

	@PostMapping("/member/orderCheckout")
	public String orderCheckout(@RequestParam("orderId") String orderId, HttpSession session, Model m,
			RedirectAttributes redirectAttributes) {
		Members member = (Members) session.getAttribute("member");
			Integer memberId = member.getMemberId();

			List<OrderDetailView> orderDetails = orderDetailService.findOrderDetail(orderId);
			m.addAttribute("orderDetails", orderDetails);
			redirectAttributes.addFlashAttribute("orderId", orderId);
			// 未更改
			return "order/recipientInfoPage";
	}
	@ResponseBody
	@PostMapping("/member/getOrderListBydate")
	public List<OrderView> getOrderListBydate(@RequestBody OrderViewByDateDto orderViewByDateDto,HttpSession session){
		Members member = (Members) session.getAttribute("member");
		Integer memberId = member.getMemberId();
		LocalDate startDate = orderViewByDateDto.getStartDate();
		LocalDate endDate = orderViewByDateDto.getEndDate();
		String orderStatusId = orderViewByDateDto.getOrderStatusId();
		
		return orderService.findAllByDate(memberId, orderStatusId, startDate, endDate);
		
	}
	
//  不確定會不會用暫時先放者
//	@GetMapping("/checkout")
//	public String checkoutGet(HttpSession session, Model m) {
//		Members member = (Members)session.getAttribute("member");
//		if(member != null) {
//			UUID memberId = member.getMemberId();
//			List<OrderView> allOrderByMemberId = orderService.findAllOrderByMemberId(memberId);
//			m.addAttribute("allOrderByMemberId", allOrderByMemberId);
//			return "order/orderList";
//		}
//		return "redirect:/members/login";
//	}
	//下面取代先放者
//	@GetMapping("/checkOrder")
//	public String checkOrder(@RequestParam(value = "orderStatus", defaultValue = "2") Integer orderStatus, Model m,
//			HttpSession session) {
//		Members member = (Members) session.getAttribute("member");
//		if (member != null) {
//			Integer memberId = member.getMemberId();
//			if (orderStatus != 0) {
//				List<OrderView> ordersByStatus = orderService.findOrdersByStatus(memberId, orderStatus);
//				m.addAttribute("allOrderByMemberId", ordersByStatus);
//				return "order/orderList";
//			} else {
//				List<OrderView> ordersByStatus = orderService.findAllOrderByMemberId(memberId);
//				m.addAttribute("allOrderByMemberId", ordersByStatus);
//				return "order/orderList";
//			}
//		} else {
//
//			return "redirect:/members/login";
//		}
//	}

	@GetMapping("/member/checkOrder")
	public String checkOrder(Model m, HttpSession session) {
		Members member = (Members) session.getAttribute("member");
			Integer memberId = member.getMemberId();
			List<OrderView> allOrdersForTheCurrentMonth = orderService.findAllByDate(memberId, "", null, null);
			for(OrderView orderView : allOrdersForTheCurrentMonth) {
				System.out.println(orderView.getOrderId());
			}
			List<MonthlyOrderTotalsWithMembers> monthlyOrderTotalsWithMembers = orderService.findMonthlyOrderTotalsWithMembers(memberId);
			for(MonthlyOrderTotalsWithMembers md : monthlyOrderTotalsWithMembers) {
				System.out.println(md);
			}
			m.addAttribute("monthlyOrderTotalsWithMembers",monthlyOrderTotalsWithMembers);
			m.addAttribute("allOrderByMemberId", allOrdersForTheCurrentMonth);
			return "order/orderList";
	}
	
	@GetMapping("/member/details")
	public String getOrderDetails(Model model) {
		List<Coupon> coupons = couponService.getAllCoupons();
	    model.addAttribute("coupons", coupons);
        return "order/recipientInfoPage"; // 返回你的 Thymeleaf 模板名稱
    }

}
