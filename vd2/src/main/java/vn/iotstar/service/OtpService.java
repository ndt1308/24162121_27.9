package vn.iotstar.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.iotstar.entity.OtpPurpose;
import vn.iotstar.entity.OtpToken;
import vn.iotstar.exception.BusinessException;
import vn.iotstar.repository.OtpTokenRepository;

@Service
@RequiredArgsConstructor
public class OtpService {

	public enum Result {
		OK, INVALID, EXPIRED, TOO_MANY_ATTEMPTS
	}

	private final OtpTokenRepository repo;
	private final PasswordEncoder encoder;
	private final MailService mail;

	@Value("${app.otp.expiry-minutes:5}")
	private long expiryMinutes;
	@Value("${app.otp.max-attempts:5}")
	private int maxAttempts;
	@Value("${app.otp.resend-seconds:60}")
	private long resendSeconds;

	private final SecureRandom random = new SecureRandom();

	/** Sinh OTP mới (xóa OTP cũ cùng mục đích) và gửi mail. */
	@Transactional
	public void send(String email, OtpPurpose purpose) {
		repo.findFirstByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, purpose).ifPresent(last -> {
			if (last.getCreatedAt().plusSeconds(resendSeconds).isAfter(LocalDateTime.now())) {
				throw new BusinessException("Vui lòng chờ " + resendSeconds + " giây trước khi yêu cầu mã mới.");
			}
		});
		repo.deleteByEmailAndPurpose(email, purpose);

		String code = String.format("%06d", random.nextInt(1_000_000));
		OtpToken t = new OtpToken();
		t.setEmail(email);
		t.setPurpose(purpose);
		t.setCodeHash(encoder.encode(code));
		t.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
		repo.save(t);

		mail.sendOtp(email, code, purpose, expiryMinutes);
	}

	/**
	 * Trả về Result thay vì ném exception: nếu ném exception trong transaction thì số lần
	 * nhập sai (attempts) sẽ bị rollback và kẻ tấn công có thể đoán OTP không giới hạn.
	 */
	@Transactional
	public Result verify(String email, String code, OtpPurpose purpose) {
		Optional<OtpToken> opt = repo.findFirstByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, purpose);
		if (opt.isEmpty()) {
			return Result.EXPIRED;
		}
		OtpToken t = opt.get();
		if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
			return Result.EXPIRED;
		}
		if (t.getAttempts() >= maxAttempts) {
			return Result.TOO_MANY_ATTEMPTS;
		}
		if (code == null || !encoder.matches(code, t.getCodeHash())) {
			t.setAttempts(t.getAttempts() + 1);
			repo.save(t);
			return Result.INVALID;
		}
		t.setUsed(true);
		repo.save(t);
		return Result.OK;
	}
}
