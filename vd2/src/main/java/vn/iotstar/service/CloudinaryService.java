package vn.iotstar.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.iotstar.exception.BusinessException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

	private static final long MAX_SIZE = 5L * 1024 * 1024;

	private final Cloudinary cloudinary;

	public record UploadResult(String url, String publicId) {
	}

	public UploadResult upload(MultipartFile file) {
		String type = file.getContentType();
		if (type == null || !type.startsWith("image/")) {
			throw new BusinessException("Chỉ chấp nhận file ảnh.");
		}
		if (file.getSize() > MAX_SIZE) {
			throw new BusinessException("Ảnh tối đa 5MB.");
		}
		try {
			Map<?, ?> r = cloudinary.uploader().upload(file.getBytes(),
					ObjectUtils.asMap("folder", "iotstar/products", "resource_type", "image"));
			return new UploadResult((String) r.get("secure_url"), (String) r.get("public_id"));
		} catch (IOException e) {
			log.error("Upload Cloudinary thất bại", e);
			throw new BusinessException("Không upload được ảnh. Vui lòng thử lại.");
		}
	}

	public void delete(String publicId) {
		if (publicId == null || publicId.isBlank()) {
			return;
		}
		try {
			cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
		} catch (Exception e) {
			log.warn("Không xóa được ảnh {} trên Cloudinary", publicId, e);
		}
	}
}
