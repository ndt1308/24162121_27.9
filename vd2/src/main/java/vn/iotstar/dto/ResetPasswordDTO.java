package vn.iotstar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDTO {
	@NotBlank
	@Email
	private String email;

	@Pattern(regexp = "\\d{6}", message = "Mã OTP gồm 6 chữ số")
	private String otp;

	@Size(min = 6, max = 100, message = "Mật khẩu từ 6 đến 100 ký tự")
	private String newPassword;

	@NotBlank(message = "Vui lòng nhập lại mật khẩu")
	private String confirmPassword;
}
