package vn.iotstar.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

/** Principal tuỳ biến để header.html hiển thị fullName, email, role. */
@Getter
public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private final Long id;
	private final String email;
	private final String password;
	private final String fullName;
	private final String roleName; // USER / ADMIN
	private final boolean enabled;

	public CustomUserDetails(Long id, String email, String password, String fullName, String roleName,
			boolean enabled) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.fullName = fullName;
		this.roleName = roleName;
		this.enabled = enabled;
	}

	public boolean isAdmin() {
		return "ADMIN".equalsIgnoreCase(roleName);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
	}

	@Override
	public String getPassword() {
		return password;
	}

	/** Đăng nhập bằng email nên username = email */
	@Override
	public String getUsername() {
		return email;
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

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
