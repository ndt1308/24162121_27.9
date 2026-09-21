package vn.iotstar.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Không dùng @Data: hashCode/toString của quan hệ 2 chiều User <-> Product sẽ đệ quy vô hạn.
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Tên đăng nhập (lưu chữ thường, không chứa '@' để không trùng với email khi đăng nhập). */
	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, unique = true, length = 120)
	private String email;

	@Column(nullable = false, length = 100)
	private String password;

	@Column(nullable = false, length = 120, columnDefinition = "nvarchar(120)")
	private String fullName;

	/** URL ảnh đại diện (Cloudinary hoặc /images/...). Null -> header dùng ảnh mặc định. */
	@Column(length = 500)
	private String images;

	@Column(nullable = false)
	private boolean enabled = false;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Product> products = new ArrayList<>();
}
