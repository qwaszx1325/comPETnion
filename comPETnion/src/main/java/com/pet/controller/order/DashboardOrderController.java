package com.pet.controller.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pet.model.admin.Admin;
import com.pet.model.order.OrderDetailView;
import com.pet.model.order.Orders;
import com.pet.service.member.MembersService;
import com.pet.service.order.OrderDetailService;
import com.pet.service.order.OrderService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardOrderController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderDetailService orderDetailService;
	

	@GetMapping("/admin/dashboard/orderStatus")
	public String deshboardOrderStatus(Model m, HttpSession session) {
		Admin admin = (Admin) session.getAttribute("admin");
			List<Orders> allOrders = orderService.findAll();
			m.addAttribute("allOrders", allOrders);
			return "order/indexBackOrder";
	}

	@GetMapping("/admin/dashboard/orderDetail")
	public String dashboardOrderDetail(@RequestParam("orderId") String orderId, Model m, HttpSession session) {
			List<OrderDetailView> orderDetails = orderDetailService.findOrderDetail(orderId);
			m.addAttribute("orderDetails", orderDetails);
			Orders order = orderDetailService.findRecipientInfoByOrderId(orderId);
			
			m.addAttribute("order", order);
			return "order/dashboardOrderDetailList";
	}

	@PostMapping("/admin/dashboard/updateOrderStatus")
	public String deshboardUpdateOrderStatus(@RequestParam("orderId") String orderId,
			@RequestParam("orderStatusId") Integer orderStatusId, HttpSession session) {

		Admin admin = (Admin) session.getAttribute("admin");
			orderService.updateOrderStatus(orderId, orderStatusId);
			try {
				if (orderStatusId == 3) {
					orderService.sendMailForOrder(orderId, "訂單已到貨");
				} else if (orderStatusId == 4) {
					orderService.sendMailForOrder(orderId, "訂單已取消");
				}
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "redirect:/admin/dashboard/orderStatus";
	}

}
