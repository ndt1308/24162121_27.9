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

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User u = users.findByEmailWithRole(email.trim())
				.orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản."));
		return new CustomUserDetails(u.getId(), u.getEmail(), u.getPassword(), u.getFullName(),
				u.getRole().getName(), u.isEnabled());
	}
}
