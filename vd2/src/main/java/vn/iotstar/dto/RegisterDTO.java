package vn.iotstar.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
	@NotBlank(message = "Họ tên không được để trống")
	@Size(max = 120)
	private String fullName;

	@NotBlank(message = "Username không được để trống")
	@Pattern(regexp = "^[A-Za-z0-9._-]{3,50}$", message = "Username 3-50 ký tự, chỉ gồm chữ, số, dấu . _ -")
	private String username;

	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	private String email;

	@Size(min = 6, max = 100, message = "Mật khẩu từ 6 đến 100 ký tự")
	private String password;

	@NotBlank(message = "Vui lòng nhập lại mật khẩu")
	private String confirmPassword;
}
