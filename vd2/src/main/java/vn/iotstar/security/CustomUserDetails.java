package vn.iotstar.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

/**
 * Principal lưu trong session sau khi đăng nhập. Ngoài username/password còn mang thêm id, email,
 * fullName, images để header.html hiển thị mà không cần truy vấn DB.
 */
@Getter
public class CustomUserDetails implements UserDetails, CredentialsContainer {

	private static final long serialVersionUID = 1L;

	private final Long id;
	private final String username;
	private final String email;
	private String password;
	private final String fullName;
	private final String images;
	/** Tên role gốc trong DB, vd: ADMIN / USER */
	private final String role;
	private final boolean enabled;

	public CustomUserDetails(Long id, String username, String email, String password, String fullName,
			String images, String role, boolean enabled) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.fullName = fullName;
		this.images = images;
		this.role = role;
		this.enabled = enabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role; // hasRole('ADMIN') cần tiền tố ROLE_
		return List.of(new SimpleGrantedAuthority(authority));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/** Xóa hash mật khẩu khỏi principal sau khi xác thực xong để không nằm lại trong session. */
	@Override
	public void eraseCredentials() {
		this.password = null;
	}
}
