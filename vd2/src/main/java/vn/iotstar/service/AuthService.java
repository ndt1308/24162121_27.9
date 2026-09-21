package vn.iotstar.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.dto.ResetPasswordDTO;
import vn.iotstar.entity.OtpPurpose;
import vn.iotstar.entity.User;
import vn.iotstar.exception.BusinessException;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository users;
	private final RoleRepository roles;
	private final OtpService otp;
	private final PasswordEncoder encoder;

	private static String norm(String email) {
		return email == null ? "" : email.trim().toLowerCase();
	}

	/** Tạo tài khoản chưa kích hoạt (enabled=false) và gửi OTP. Đăng ký lại khi chưa xác thực sẽ ghi đè thông tin. */
	@Transactional
	public void register(RegisterDTO dto) {
		String email = norm(dto.getEmail());
		String username = norm(dto.getUsername());
		User existing = users.findByEmailIgnoreCase(email).orElse(null);
		if (existing != null && existing.isEnabled()) {
			throw new BusinessException("Email đã được đăng ký.");
		}
		// username đã thuộc về tài khoản KHÁC (không phải chính bản ghi chưa kích hoạt đang đăng ký lại)
		users.findByUsernameIgnoreCase(username).ifPresent(other -> {
			if (existing == null || !other.getId().equals(existing.getId())) {
				throw new BusinessException("Username đã tồn tại.");
			}
		});
		User u = existing;
		if (u == null) {
			u = new User();
			u.setEmail(email);
			u.setEnabled(false);
			u.setRole(roles.findByNameIgnoreCase("USER")
					.orElseThrow(() -> new BusinessException("Hệ thống chưa khởi tạo role USER.")));
		}
		u.setUsername(username);
		u.setFullName(dto.getFullName().trim());
		u.setPassword(encoder.encode(dto.getPassword()));
		users.save(u);
		otp.send(email, OtpPurpose.REGISTER);
	}

	@Transactional
	public OtpService.Result verifyRegister(String email, String code) {
		String e = norm(email);
		OtpService.Result r = otp.verify(e, code, OtpPurpose.REGISTER);
		if (r == OtpService.Result.OK) {
			users.findByEmailIgnoreCase(e).ifPresent(u -> u.setEnabled(true));
		}
		return r;
	}

	@Transactional
	public void resendRegisterOtp(String email) {
		String e = norm(email);
		User u = users.findByEmailIgnoreCase(e)
				.filter(x -> !x.isEnabled())
				.orElseThrow(() -> new BusinessException("Tài khoản không tồn tại hoặc đã được xác thực."));
		otp.send(u.getEmail(), OtpPurpose.REGISTER);
	}

	/** Không tiết lộ email có tồn tại hay không: chỉ gửi OTP khi tài khoản hợp lệ. */
	@Transactional
	public void forgotPassword(String email) {
		users.findByEmailIgnoreCase(norm(email)).filter(User::isEnabled)
				.ifPresent(u -> otp.send(u.getEmail(), OtpPurpose.RESET));
	}

	@Transactional
	public OtpService.Result resetPassword(ResetPasswordDTO dto) {
		String e = norm(dto.getEmail());
		OtpService.Result r = otp.verify(e, dto.getOtp(), OtpPurpose.RESET);
		if (r == OtpService.Result.OK) {
			users.findByEmailIgnoreCase(e).ifPresent(u -> u.setPassword(encoder.encode(dto.getNewPassword())));
		}
		return r;
	}
}
