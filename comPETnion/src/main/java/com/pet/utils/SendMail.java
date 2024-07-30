package com.pet.utils;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SendMail {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private TemplateEngine templateEngine;

	public void sendSimpleEmail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("qqq13258596@gmail.com");
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}

	// 可以傳送html格式的mail
	public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setFrom("your-email@gmail.com");
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlBody, true);

		mailSender.send(message);
	}

	// 領養申請
	@Async
	public void sendApplicationRequest(String to, String applicantName, Integer petCaseId, Date applyTime)
			throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		helper.setFrom("your-email@gmail.com");
		helper.setTo(to);
		helper.setSubject("寵物領養申請通知");

		Context context = new Context();
		context.setVariable("applicantName", applicantName);
		context.setVariable("petCaseId", petCaseId);
		context.setVariable("applyTime", applyTime);
		String htmlContent = templateEngine.process("/adopt/mail/ApplicationRequest", context);

		helper.setText(htmlContent, true);

		mailSender.send(mimeMessage);
	}

	// 案件審核回覆（會員to會員）
	@Async
	public void sendApplicationResponse(String to, Integer petCaseId) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		helper.setFrom("your-email@gmail.com");
		helper.setTo(to);
		helper.setSubject("寵物領養回覆通知");

		Context context = new Context();
		context.setVariable("petCaseId", petCaseId);
		String htmlContent = templateEngine.process("/adopt/mail/ApplicationResponse", context);

		helper.setText(htmlContent, true);

		mailSender.send(mimeMessage);
	}

	// 活動審核通知
	@Async
	public void sendEventApproval(String to, Integer eventId, String eventSubject, Integer approvalStatus)
			throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		helper.setFrom("your-email@gmail.com");
		helper.setTo(to);
		helper.setSubject("活動審核通知");

		Context context = new Context();
		context.setVariable("eventId", eventId);
		context.setVariable("eventSubject", eventSubject);

		String htmlPath = null;
		if (approvalStatus == 2) {
			htmlPath = "/event/mail/eventApprovalMail";
		} else if (approvalStatus == 3) {
			htmlPath = "/event/mail/eventRejectionMail";
		}

		String htmlContent = templateEngine.process(htmlPath, context);
		helper.setText(htmlContent, true);

		mailSender.send(mimeMessage);
	}

	// 活動修改通知
	@Async
	public void sendEventUpdate(List<String> toList, String hostMemberName, String eventSubject)
			throws MessagingException {
		for (String to : toList) {

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setFrom("your-email@gmail.com");
			helper.setTo(to);
			helper.setSubject("活動修改通知");

			Context context = new Context();
			context.setVariable("hostMemberName", hostMemberName);
			context.setVariable("eventSubject", eventSubject);
			String htmlContent = templateEngine.process("/event/mail/eventUpdateMail", context);

			helper.setText(htmlContent, true);

			mailSender.send(mimeMessage);
		}
	}


	// 活動取消通知
	@Async
	public void sendEventCancel(List<String> toList, String hostMemberName, String eventSubject, Date eventDate)
			throws MessagingException {
		for (String to : toList) {

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setFrom("your-email@gmail.com");
			helper.setTo(to);
			helper.setSubject("活動取消通知");

			Context context = new Context();
			context.setVariable("hostMemberName", hostMemberName);
			context.setVariable("eventSubject", eventSubject);
			context.setVariable("eventDate", eventDate);
			String htmlContent = templateEngine.process("/event/mail/eventCancelMail", context);

			helper.setText(htmlContent, true);

			mailSender.send(mimeMessage);
		}
	}

	// 活動報名成功通知
	@Async
	public void sendRegistrationSuccess(String to, String hostMemberName, String eventSubject, Date eventDate,
			String eventPlaceName, String eventPlaceAddress) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		helper.setFrom("your-email@gmail.com");
		helper.setTo(to);
		helper.setSubject("報名成功");

		Context context = new Context();
		context.setVariable("hostMemberName", hostMemberName);
		context.setVariable("eventSubject", eventSubject);
		context.setVariable("eventDate", eventDate);
		context.setVariable("eventPlaceName", eventPlaceName);
		context.setVariable("eventPlaceAddress", eventPlaceAddress);
		String htmlContent = templateEngine.process("/event/mail/registrationSuccessMail", context);

		helper.setText(htmlContent, true);

		mailSender.send(mimeMessage);
	}
	
	//報名截止 發送參加者清單給主辦
	@Async
	public void sendParticipantList(String to, Integer eventId, String eventSubject, String csvContent)
			throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		helper.setFrom("your-email@gmail.com");
		helper.setTo(to);
		helper.setSubject("活動報名截止通知");

		Context context = new Context();
		context.setVariable("eventId", eventId);
		context.setVariable("eventSubject", eventSubject);

		String htmlContent = templateEngine.process("/event/mail/closingDateDueMail", context);

		helper.setText(htmlContent, true);
		
		helper.addAttachment("參加名單.csv", new ByteArrayResource(csvContent.getBytes()));

		mailSender.send(mimeMessage);
	}

}
