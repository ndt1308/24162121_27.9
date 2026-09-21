package vn.iotstar.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "otp_tokens", indexes = @Index(name = "idx_otp_email_purpose", columnList = "email,purpose"))
public class OtpToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String email;

	/** Lưu BCrypt hash của mã OTP, không lưu mã gốc */
	@Column(nullable = false, length = 100)
	private String codeHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OtpPurpose purpose;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	@Column(nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(nullable = false)
	private int attempts;

	@Column(nullable = false)
	private boolean used;
}
