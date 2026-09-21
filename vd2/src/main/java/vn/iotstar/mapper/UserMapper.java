package vn.iotstar.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "roleId", source = "role.id")
	@Mapping(target = "roleName", source = "role.name")
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "productCount", ignore = true)
	UserDTO toDto(User entity);

	@Mapping(target = "role", ignore = true)
	@Mapping(target = "products", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	User toEntity(UserDTO dto);

	/** Cập nhật entity có sẵn từ DTO (username, email, mật khẩu, vai trò do service xử lý riêng) */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "username", ignore = true)
	@Mapping(target = "email", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "products", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	void updateEntity(UserDTO dto, @MappingTarget User entity);
}
