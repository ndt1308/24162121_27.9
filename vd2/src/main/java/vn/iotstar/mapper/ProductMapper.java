package vn.iotstar.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

	@Mapping(target = "ownerEmail", source = "user.email")
	@Mapping(target = "ownerName", source = "user.fullName")
	ProductDTO toDto(Product entity);

	@Mapping(target = "user", ignore = true)
	@Mapping(target = "imagePublicId", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	Product toEntity(ProductDTO dto);

	/** Ảnh và chủ sở hữu do service xử lý riêng */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "imageUrl", ignore = true)
	@Mapping(target = "imagePublicId", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	void updateEntity(ProductDTO dto, @MappingTarget Product entity);
}
