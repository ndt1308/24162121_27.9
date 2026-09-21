package vn.iotstar.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
	private Long id;

	@NotBlank(message = "Username không được để trống")
	@Pattern(regexp = "^[A-Za-z0-9._-]{3,50}$", message = "Username 3-50 ký tự, chỉ gồm chữ, số, dấu . _ -")
	private String username;

	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không hợp lệ")
	private String email;

	@NotBlank(message = "Họ tên không được để trống")
	private String fullName;

	/** URL ảnh đại diện, để trống = dùng ảnh mặc định */
	@Size(max = 500, message = "Đường dẫn ảnh tối đa 500 ký tự")
	@Pattern(regexp = "^(https?://\\S+|/[^/\\s]\\S*)?$", message = "Ảnh phải là đường dẫn bắt đầu bằng http://, https:// hoặc /")
	private String images;

	/** Chỉ dùng để nhập từ form; mapper không bao giờ copy mật khẩu ra DTO. Bỏ trống khi sửa = giữ nguyên. */
	private String password;

	@NotNull(message = "Vui lòng chọn vai trò")
	private Long roleId;
	private String roleName;

	private boolean enabled = true;
	private long productCount;
	private LocalDateTime createdAt;
}
