package vn.iotstar.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200, columnDefinition = "nvarchar(200)")
	private String name;

	@Column(length = 1000, columnDefinition = "nvarchar(1000)")
	private String description;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal price;

	@Column(length = 500)
	private String imageUrl;

	/** public_id của Cloudinary, dùng để xóa ảnh khi sửa/xóa sản phẩm */
	@Column(length = 200)
	private String imagePublicId;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}
