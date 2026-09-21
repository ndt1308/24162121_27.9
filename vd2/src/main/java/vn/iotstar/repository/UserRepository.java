package vn.iotstar.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.iotstar.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailIgnoreCase(String email);

	Optional<User> findByUsernameIgnoreCase(String username);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByUsernameIgnoreCase(String username);

	@EntityGraph(attributePaths = "role")
	Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
			String username, String email, String fullName, Pageable pageable);

	/**
	 * Đăng nhập bằng username HOẶC email (không phân biệt hoa thường). Username không được chứa '@'
	 * nên một chuỗi đăng nhập không bao giờ khớp cùng lúc 2 tài khoản khác nhau.
	 */
	@Query("""
			SELECT u FROM User u JOIN FETCH u.role
			WHERE lower(u.username) = lower(:login) OR lower(u.email) = lower(:login)
			""")
	Optional<User> findByLoginWithRole(@Param("login") String login);
}
