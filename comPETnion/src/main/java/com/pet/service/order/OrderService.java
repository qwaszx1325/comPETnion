package com.pet.service.order;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pet.dto.order.RecipientInfoDto;
import com.pet.model.member.Members;
import com.pet.model.order.EcpayTransactions;
import com.pet.model.order.MonthlyOrderTotalsWithMembers;
import com.pet.model.order.OrderDetail;
import com.pet.model.order.OrderDetailView;
import com.pet.model.order.OrderView;
import com.pet.model.order.Orders;
import com.pet.repository.member.MembersRepository;
import com.pet.repository.order.EcpayTransactionsRepository;
import com.pet.repository.order.MonthlyOrderTotalsWithMembersRepository;
import com.pet.repository.order.OrderDetailRepository;
import com.pet.repository.order.OrderRepository;
import com.pet.repository.order.OrderViewRepository;
import com.pet.repository.order.ShoppingCartRepository;
import com.pet.utils.ECPayUtils;
import com.pet.utils.SendMail;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import ecpay.payment.integration.exception.EcpayException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
@Transactional
public class OrderService {
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderDetailRepository orderDetailRepository;
	@Autowired
	private EcpayTransactionsRepository ecpayTransactionsRepository;
	@Autowired
	private ShoppingCartRepository shoppingCartRepository;
	@Autowired
	private MonthlyOrderTotalsWithMembersRepository monthlyOrderTotalsWithMembersRepository;
	@Autowired
	private SendMail sendMail;
	
//	@Value("${your.website.url}")
//	private String websiteUrl; //ngrok網址

	@Autowired
	private EntityManager entityManager;

	// 結帳
	public String checkout(String itemsJson, String totalPrice, Integer memberId) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter orderIduse = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

		String orderId = "pet" + now.format(orderIduse) + "co";
		Orders order = new Orders();

		order.setOrderId(orderId);
		int totalPriceInt = Integer.parseInt(totalPrice);
		order.setMemberId(memberId);
		order.setTotalCost(totalPriceInt);
		order.setOrderStatusId(1);
		orderRepository.save(order);

		List<OrderDetail> orderDetailList = new ArrayList<>();

		// 解析 JSON 字符串
		JSONArray itemsArray = new JSONArray(itemsJson);
		for (int i = 0; i < itemsArray.length(); i++) {
			JSONObject item = itemsArray.getJSONObject(i);
			int productId = item.getInt("productId");
			int quantity = item.getInt("quantity");
			int price = item.getInt("price");
			int itemTotalCost = (quantity * price);

			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setProductId(productId);
			orderDetail.setQuantity(quantity);
			orderDetail.setCost(itemTotalCost);
			orderDetail.setOrderBean(order); // 設置關聯
			orderDetail.setIsReviewed(false);

			orderDetailList.add(orderDetail);

			shoppingCartRepository.deleteAllShoopingCart(memberId, productId);

		}
		// chatGPT教的
		for (OrderDetail orderDetail : orderDetailList) {
			orderDetailRepository.save(orderDetail); // 假設這個方法會持久化訂單明細
		}
		return orderId;

	}
//	public void addShippingCost(String orderId,Integer shippingCost) {
//		Optional<Orders> optionalOrder = orderRepository.findById(orderId);
//		Orders order = optionalOrder.get();
//		order.setShippingCost(shippingCost);
//		Integer beforeTotalCost = order.getTotalCost();
//		int nowTotalCost = beforeTotalCost + shippingCost;
//		order.setShippingCost(nowTotalCost);
//		orderRepository.save(order);
//		
//	}

	// 訂單日期查詢
	public List<OrderView> findAllByDate(Integer memberId, String orderStatusId, LocalDate startDate,
			LocalDate endDate) {
		LocalDate today = LocalDate.now();
		LocalDate startDateDefault = today.with(TemporalAdjusters.firstDayOfMonth());
		LocalDate endDateByMonth = today.with(TemporalAdjusters.firstDayOfNextMonth());

		List<Integer> orderStatusIds;

		if (orderStatusId == null || "".equals(orderStatusId)) {
			orderStatusIds = Arrays.asList(1, 2, 3, 4);
		} else {
			orderStatusIds = Arrays.asList(Integer.valueOf(orderStatusId));
		}

		if (startDate == null && endDate == null) {
			startDate = startDateDefault;
			endDate = endDateByMonth;
		} else if (startDate == null) {
			startDate = startDateDefault;
		} else if (endDate == null) {
			endDate = endDateByMonth;
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrderView> query = cb.createQuery(OrderView.class);
		Root<OrderView> orderViewRoot = query.from(OrderView.class);

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.equal(orderViewRoot.get("memberId"), memberId));
		predicates.add(orderViewRoot.get("orderStatusId").in(orderStatusIds));
		predicates.add(cb.between(orderViewRoot.get("createDate"), startDate, endDate));

		System.out.println(orderViewRoot.get("memberId"));
		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}

	public List<MonthlyOrderTotalsWithMembers> findMonthlyOrderTotalsWithMembers(Integer memberId) {
		return monthlyOrderTotalsWithMembersRepository.findByMemberId(memberId);
	}

	public List<OrderView> findAllOrderByMemberId(Integer memberId) {
		return orderRepository.findAllOrderByMemberId(memberId);
	}

	public String ecpayCheckout(String orderId) {

		Optional<Orders> orderOptional = orderRepository.findById(orderId);
		List<OrderDetailView> orderDetail = orderDetailRepository.findByOrderId(orderId);
		Orders orders = orderOptional.get();
		AllInOne all = new AllInOne("");

		Date createTime = orders.getCreateTime();
		// 定義 SimpleDateFormat 來格式化 Date
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		// 將 Date 格式化為指定格式的字符串
		String formattedDate = simpleDateFormat.format(createTime);

		// 網址

//		System.out.println(test);
		AioCheckOutALL obj = new AioCheckOutALL();
//		System.out.println(orders.getOrderId().toString());
		obj.setMerchantTradeNo(orders.getOrderId().toString());
		obj.setMerchantTradeDate(formattedDate);
		obj.setTotalAmount((orders.getTotalCost() + ""));
		// 3002599
		obj.setTradeDesc("test Description");
		// 可以用#加進去多個商品
		String productList = "";
		for (int i = 0; i < orderDetail.size(); i++) {
			if (i > 0) {
				productList +="#";
			}
			
			productList += orderDetail.get(i).getProductTitle()+" x "+orderDetail.get(i).getQuantity()+" = "+orderDetail.get(i).getCost();
		}
		productList += "#運費" + " = " + orders.getShippingCost();

		obj.setItemName(productList);
		// 交易結果回傳網址，只接受 https 開頭的網站，可以使用 ngrok

		// 交易結果
		obj.setReturnURL("http://localhost:8081/comPETnion/backendReturn");

		// 付款完成跳轉結果
		obj.setClientBackURL( "http://localhost:8081/comPETnion/frontendReturn?orderId=" + orders.getOrderId().toString());
		obj.setNeedExtraPaidInfo("N");
		// 商店轉跳網址 (Optional)
		try {
			String form = all.aioCheckOut(obj, null);
			return form;
		} catch (EcpayException e) {
			System.err.println("ECPay Exception: " + e.getMessage());
			throw e;
		}
	}

	public void checkOrderStatus(String orderId) {
//		// 綠界傳送到後台的資料
		orderRepository.updateOrderStatus(orderId, 2);
//		System.out.println("Backend return: " + params);
//
//		// 檢查 CheckMacValue(因目前是用測試店家所以可以去測試店家裡面找到這兩個值)店家的HashKey 店家的HashIV
//		boolean isValid = ECPayUtils.verifyCheckMacValue(params, "5294y06JbISpM5x9", "v77hoKGq4kWxNNIS");
//
//		EcpayTransactions ecpayTransactions = new EcpayTransactions();
//		ecpayTransactions.setMerchantId(params.get("MerchantID"));
//		ecpayTransactions.setOrderId(params.get("MerchantTradeNo"));
//		ecpayTransactions.setPaymentDate(params.get("PaymentDate"));
//		ecpayTransactions.setPaymentType(params.get("PaymentType"));
//		ecpayTransactions.setRtnCode(params.get("RtnCode"));
//		ecpayTransactions.setRtnMsg(params.get("RtnMsg"));
//		ecpayTransactions.setTradeAmt(params.get("TradeAmt"));
//		ecpayTransactions.setTradeDate(params.get("TradeDate"));
//		ecpayTransactions.setTradeNo(params.get("TradeNo"));
//		ecpayTransactionsRepository.save(ecpayTransactions);
//
//		// 1為交易成功狀態 其餘為交易失敗
//		if ("1".equals(params.get("RtnCode"))) {
//			String orderId = params.get("MerchantTradeNo");
//			Integer orderStatus = 2;

//		} else {
//			System.out.println("交易失敗");
//		}

	}

	// 新增收件人訊息且更新訂單總價
	public void saveRecipientInfo(RecipientInfoDto recipientInfoDto) {

		String orderId = recipientInfoDto.getOrderId();
		Optional<Orders> orders = orderRepository.findById(orderId);
		Orders order = orders.get();
		Integer shippingCost = recipientInfoDto.getShippingCost();
		Integer totalPrice = recipientInfoDto.getTotalPrice();

		System.out.println(totalPrice);
		order.setAddress(recipientInfoDto.getAddress());
		order.setEmail(recipientInfoDto.getEmail());
		order.setOrderId(orderId);
		order.setPhone(recipientInfoDto.getPhone());
		order.setRecipientName(recipientInfoDto.getRecipientName());
		order.setShippingCost(shippingCost);
		System.out.println(shippingCost);
		if (shippingCost == 100) {
			LocalDate threeDaysLater = LocalDate.now().plusDays(3);
			order.setEstimatedArrivalDate(threeDaysLater);
		} else {
			LocalDate oneDaysLater = LocalDate.now().plusDays(1);
			order.setEstimatedArrivalDate(oneDaysLater);
		}
		order.setOrderId(orderId);
		order.setTotalCost(totalPrice);

		orderRepository.save(order);
	}

	public void updateOrderStatus(String orderId, Integer status) {
		orderRepository.updateOrderStatus(orderId, status);
	}

	public List<OrderView> findOrdersByStatus(Integer memberId, Integer orderStatusId) {
		List<OrderView> allOrderByStatus = orderRepository.findAllOrderByStatus(memberId, orderStatusId);
		return allOrderByStatus;
	}

	public List<Orders> findAll() {
		List<Orders> allList = orderRepository.findAll();
		return allList;
	}

	public void sendMailForOrder(String orderId, String subject) throws MessagingException {

		Optional<Orders> byId = orderRepository.findById(orderId);
		Orders orders = byId.get();
		String email = orders.getEmail();
		String recipientName = orders.getRecipientName();

		String wirteHTML = wirteHTML(recipientName, orderId);

		sendMail.sendHtmlEmail(email, subject, wirteHTML);
	}

	public String wirteHTML(String memberName, String orderId) {

		LocalDate currentDate = LocalDate.now();

		// 定義日期格式
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// 格式化日期
		String formattedDate = currentDate.format(formatter);

		String orderLink = "http://localhost:8081/comPETnion/member/checkOrder"; // 訂單連結的網址

		String emailContent = String.format("""
				<!DOCTYPE html>
				<html lang="zh-TW">
				<head>
				    <meta charset="UTF-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1.0">
				    <title>訂單到貨通知</title>
				    <style>
				                  body {
				                      font-family: Arial, sans-serif;
				                      background-color: #f4f4f4;
				                      margin: 0;
				                      padding: 0;
				                  }
				                  .container {
				                      width: 100%%;
				                      max-width: 600px;
				                      margin: 0 auto;
				                      background-color: #ffffff;
				                      padding: 20px;
				                      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
				                  }
				                  .header {
				                      text-align: center;
				                      padding: 20px 0;
				                  }
				                  .header img {
				                      max-width: 100px;
				                      height: auto;
				                  }
				                  .content {
				                      margin: 20px 0;
				                  }
				                  .content h1 {
				                      color: #333333;
				                  }
				                  .content p {
				                      line-height: 1.6;
				                      color: #666666;
				                  }
				                  .footer {
				                      text-align: center;
				                      padding: 20px 0;
				                      color: #999999;
				                      font-size: 12px;
				                  }
				                  .button {
				                      display: inline-block;
				                      padding: 10px 20px;
				                      margin-top: 20px;
				                      background-color: #876d5a;
				                      color: white !important;
				                      text-decoration: none;
				                      border-radius: 5px;
				                  }
				                  table {
				                      width: 100%%;
				                      border-collapse: collapse;
				                  }
				                  td {
				                      padding: 10px;
				                      text-align: center;
				                  }
				    </style>
				</head>
				<body>
				    <div class="container">

				        <img src="https://i.imgur.com/M34EE1U.png" alt="Company Logo">

				        <div class="content">
				            <h1>訂單到貨通知</h1>
				            <p>親愛的 %s，</p>
				            <p>您的訂單已經到貨並準備送達：</p>
				            <p>
				                訂單編號：<strong>%s</strong><br>
				                到貨日期：<strong>%s</strong>
				            </p>
				            <a href="%s" class="button">查看訂單詳情</a>
				            <p>感謝您的購買，我們期待您的再次光臨！</p>
				            <p>祝您有美好的一天！</p>
				        </div>
				        <div class="footer">
				            <p>這是一封自動產生的郵件，請勿回覆。</p>
				            <p>© 2024 comPETnion .All rights reserved.</p>
				        </div>
				    </div>
				</body>
				</html>
				""", memberName, orderId, formattedDate, orderLink);

		return emailContent;
	}

	public List<Orders> getCompletedOrders(Integer memberId) {
		return orderRepository.findCompletedOrdersByMemberId(memberId);
	}
}
