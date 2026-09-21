package vn.iotstar.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductDTO {
	private Long id;

	@NotBlank(message = "Tên sản phẩm không được để trống")
	@Size(max = 200)
	private String name;

	@Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
	private String description;

	@NotNull(message = "Giá không được để trống")
	@DecimalMin(value = "0.0", message = "Giá phải >= 0")
	private BigDecimal price;

	private String imageUrl;
	private String ownerEmail;
	private String ownerName;
	private LocalDateTime createdAt;
}
