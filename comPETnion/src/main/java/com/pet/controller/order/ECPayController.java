package com.pet.controller.order;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import com.pet.dto.order.RecipientInfoDto;
import com.pet.model.order.OrderDetailView;
import com.pet.model.order.Orders;
import com.pet.service.order.OrderDetailService;
import com.pet.service.order.OrderService;
import com.pet.utils.ECPayUtils;

@Controller
public class ECPayController {

	@Autowired
	OrderService orderService;

	@Autowired
	OrderDetailService orderDetailService;
	
	@PostMapping("/ecpayCheckout")
	public String ecpayCheckout(@ModelAttribute RecipientInfoDto recipientInfoDto,Model model) {
		String orderId = recipientInfoDto.getOrderId();
		orderService.saveRecipientInfo(recipientInfoDto);
		
		String aioCheckOutALLForm = orderService.ecpayCheckout(orderId);
		model.addAttribute("aioCheckOutALLForm", aioCheckOutALLForm);
		return "order/ecpayCheckout";
	}

	@PostMapping("/backendReturn")
	public void handleBackendReturn(@RequestParam Map<String, String> params) {
		System.out.println(params);
		//orderService.checkOrderStatus(params);
	}

	@GetMapping("/frontendReturn")
    public String handleFrontendReturn(@RequestParam("orderId")String orderId,Model m) {
		List<OrderDetailView> orderDetail = orderDetailService.findOrderDetail(orderId);
		
		m.addAttribute("orderDetail",orderDetail);
		Orders order = orderDetailService.findRecipientInfoByOrderId(orderId);
		
		orderService.checkOrderStatus(orderId);
		m.addAttribute("order", order);
        return "order/orderPaymentSuccessful"; 
    }
}
