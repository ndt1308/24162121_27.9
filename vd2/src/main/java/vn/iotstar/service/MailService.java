package vn.iotstar.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.iotstar.entity.OtpPurpose;
import vn.iotstar.exception.BusinessException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

	private final JavaMailSender sender;

	@Value("${spring.mail.username}")
	private String from;

	public void sendOtp(String to, String code, OtpPurpose purpose, long expiryMinutes) {
		String subject = purpose == OtpPurpose.REGISTER
				? "IOTSTAR SHOP - Mã xác thực đăng ký"
				: "IOTSTAR SHOP - Mã đặt lại mật khẩu";
		String text = "Mã OTP của bạn là: " + code + "\n"
				+ "Mã có hiệu lực trong " + expiryMinutes + " phút. Không chia sẻ mã này với bất kỳ ai.\n"
				+ "Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email.";

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(from);
		msg.setTo(to);
		msg.setSubject(subject);
		msg.setText(text);
		try {
			sender.send(msg);
		} catch (MailException e) {
			log.error("Gửi mail thất bại tới {}", to, e);
			throw new BusinessException("Không gửi được email. Vui lòng thử lại sau.");
		}
	}
}
