package vn.iotstar.security;

import org.springframework.security.core.Authentication;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	/** Email của người đang đăng nhập (auth.getName() giờ trả về username nên không dùng làm email được nữa). */
	public static String email(Authentication auth) {
		return auth.getPrincipal() instanceof CustomUserDetails u ? u.getEmail() : auth.getName();
	}
}
