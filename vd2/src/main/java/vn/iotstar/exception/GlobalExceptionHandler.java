package vn.iotstar.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	/** Lỗi nghiệp vụ không được controller bắt (vd: không tìm thấy bản ghi). */
	@ExceptionHandler(BusinessException.class)
	public String handleBusiness(BusinessException ex, Model model) {
		model.addAttribute("message", ex.getMessage());
		return "error-message";
	}
}
