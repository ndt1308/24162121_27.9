package vn.iotstar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.exception.BusinessException;
import vn.iotstar.mapper.ProductMapper;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;

/** ADMIN thao tác được mọi sản phẩm; USER chỉ thấy và sửa/xóa sản phẩm của mình. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

	private final ProductRepository products;
	private final UserRepository users;
	private final ProductMapper mapper;
	private final CloudinaryService cloudinary;

	public Page<ProductDTO> search(String keyword, int page, int size, String email, boolean admin) {
		String k = keyword == null ? "" : keyword.trim();
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		Page<Product> result = admin
				? products.findByNameContainingIgnoreCase(k, pageable)
				: products.findByUserEmailIgnoreCaseAndNameContainingIgnoreCase(email, k, pageable);
		return result.map(mapper::toDto);
	}

	public ProductDTO get(Long id, String email, boolean admin) {
		return mapper.toDto(findAuthorized(id, email, admin));
	}

	public long countAll() {
		return products.count();
	}

	public long countOf(String email) {
		return products.countByUserEmailIgnoreCase(email);
	}

	@Transactional
	public void create(ProductDTO dto, MultipartFile file, String ownerEmail) {
		User owner = users.findByEmailIgnoreCase(ownerEmail)
				.orElseThrow(() -> new BusinessException("Không tìm thấy tài khoản."));
		Product p = mapper.toEntity(dto);
		p.setUser(owner);
		if (file != null && !file.isEmpty()) {
			CloudinaryService.UploadResult r = cloudinary.upload(file);
			p.setImageUrl(r.url());
			p.setImagePublicId(r.publicId());
		}
		products.save(p);
	}

	@Transactional
	public void update(Long id, ProductDTO dto, MultipartFile file, String email, boolean admin) {
		Product p = findAuthorized(id, email, admin);
		mapper.updateEntity(dto, p);
		String oldPublicId = null;
		if (file != null && !file.isEmpty()) {
			CloudinaryService.UploadResult r = cloudinary.upload(file);
			oldPublicId = p.getImagePublicId();
			p.setImageUrl(r.url());
			p.setImagePublicId(r.publicId());
		}
		products.save(p);
		cloudinary.delete(oldPublicId); // xóa ảnh cũ sau khi đã lưu ảnh mới
	}

	@Transactional
	public void delete(Long id, String email, boolean admin) {
		Product p = findAuthorized(id, email, admin);
		String publicId = p.getImagePublicId();
		products.delete(p);
		products.flush();
		cloudinary.delete(publicId);
	}

	private Product findAuthorized(Long id, String email, boolean admin) {
		Product p = products.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy sản phẩm."));
		if (!admin && !p.getUser().getEmail().equalsIgnoreCase(email)) {
			throw new AccessDeniedException("Bạn không có quyền với sản phẩm này");
		}
		return p;
	}
}
