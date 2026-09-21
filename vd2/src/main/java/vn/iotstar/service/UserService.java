package vn.iotstar.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.exception.BusinessException;
import vn.iotstar.mapper.UserMapper;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository users;
	private final RoleRepository roles;
	private final ProductRepository products;
	private final UserMapper mapper;
	private final PasswordEncoder encoder;
	private final CloudinaryService cloudinary;

	public List<Role> roles() {
		return roles.findAll();
	}

	/** Tìm kiếm theo username/email/họ tên + phân trang + số sản phẩm của từng user */
	public Page<UserDTO> search(String keyword, int page, int size) {
		String k = keyword == null ? "" : keyword.trim();
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		Page<User> result = users.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(k, k, k,
				pageable);

		Map<Long, Long> counts = new HashMap<>();
		List<Long> ids = result.getContent().stream().map(User::getId).toList();
		if (!ids.isEmpty()) {
			for (Object[] row : products.countByUserIds(ids)) {
				counts.put((Long) row[0], (Long) row[1]);
			}
		}
		return result.map(u -> {
			UserDTO d = mapper.toDto(u);
			d.setProductCount(counts.getOrDefault(u.getId(), 0L));
			return d;
		});
	}

	public UserDTO get(Long id) {
		return mapper.toDto(find(id));
	}

	public long countAll() {
		return users.count();
	}

	@Transactional
	public void create(UserDTO dto) {
		if (dto.getPassword() == null || dto.getPassword().length() < 6) {
			throw new BusinessException("Mật khẩu tối thiểu 6 ký tự.");
		}
		String email = dto.getEmail().trim().toLowerCase();
		String username = dto.getUsername().trim().toLowerCase();
		if (users.existsByEmailIgnoreCase(email)) {
			throw new BusinessException("Email đã tồn tại.");
		}
		if (users.existsByUsernameIgnoreCase(username)) {
			throw new BusinessException("Username đã tồn tại.");
		}
		User u = mapper.toEntity(dto);
		u.setUsername(username);
		u.setEmail(email);
		u.setImages(blankToNull(dto.getImages()));
		u.setPassword(encoder.encode(dto.getPassword()));
		u.setRole(findRole(dto.getRoleId()));
		users.save(u);
	}

	@Transactional
	public void update(Long id, UserDTO dto, String currentEmail) {
		User u = find(id);
		boolean self = u.getEmail().equalsIgnoreCase(currentEmail);
		if (self && (!dto.isEnabled() || !u.getRole().getId().equals(dto.getRoleId()))) {
			throw new BusinessException("Không thể tự vô hiệu hóa hoặc đổi vai trò của chính mình.");
		}
		mapper.updateEntity(dto, u);
		u.setImages(blankToNull(dto.getImages()));
		u.setRole(findRole(dto.getRoleId()));
		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			if (dto.getPassword().length() < 6) {
				throw new BusinessException("Mật khẩu tối thiểu 6 ký tự.");
			}
			u.setPassword(encoder.encode(dto.getPassword()));
		}
	}

	@Transactional
	public void delete(Long id, String currentEmail) {
		User u = find(id);
		if (u.getEmail().equalsIgnoreCase(currentEmail)) {
			throw new BusinessException("Không thể xóa chính tài khoản đang đăng nhập.");
		}
		List<String> publicIds = u.getProducts().stream().map(Product::getImagePublicId)
				.filter(Objects::nonNull).toList();
		users.delete(u); // cascade xóa luôn product của user
		users.flush();
		publicIds.forEach(cloudinary::delete);
	}

	private static String blankToNull(String s) {
		return s == null || s.isBlank() ? null : s.trim();
	}

	private User find(Long id) {
		return users.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy người dùng."));
	}

	private Role findRole(Long roleId) {
		return roles.findById(roleId).orElseThrow(() -> new BusinessException("Vai trò không hợp lệ."));
	}
}
