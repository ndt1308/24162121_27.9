package vn.iotstar.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository users;

	/** Tham số "login" có thể là username hoặc email. */
	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		String key = login == null ? "" : login.trim();
		User u = users.findByLoginWithRole(key)
				.orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy username/email: " + key));
		return new CustomUserDetails(u.getId(), u.getUsername(), u.getEmail(), u.getPassword(), u.getFullName(),
				u.getImages(), u.getRole().getName(), u.isEnabled());
	}
}
