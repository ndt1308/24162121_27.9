package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.dto.ResetPasswordDTO;
import vn.iotstar.exception.BusinessException;
import vn.iotstar.service.AuthService;
import vn.iotstar.service.OtpService;

/** POST /login và /logout do Spring Security tự xử lý. */
@Controller
@RequiredArgsConstructor
public class AuthController {

	private final AuthService auth;

	@GetMapping("/login")
	String login() {
		return "auth/login";
	}

	// ---------- Đăng ký + xác thực OTP ----------
	@GetMapping("/register")
	String registerForm(Model model) {
		model.addAttribute("registerDTO", new RegisterDTO());
		return "auth/register";
	}

	@PostMapping("/register")
	String register(@Valid @ModelAttribute("registerDTO") RegisterDTO dto, BindingResult br, Model model,
			RedirectAttributes ra) {
		if (!br.hasFieldErrors("password") && !br.hasFieldErrors("confirmPassword")
				&& !dto.getPassword().equals(dto.getConfirmPassword())) {
			br.rejectValue("confirmPassword", "mismatch", "Mật khẩu xác nhận không khớp");
		}
		if (br.hasErrors()) {
			return "auth/register";
		}
		try {
			auth.register(dto);
		} catch (BusinessException e) {
			model.addAttribute("error", e.getMessage());
			return "auth/register";
		}
		ra.addAttribute("email", dto.getEmail().trim().toLowerCase());
		ra.addFlashAttribute("success", "Đã gửi mã OTP tới email của bạn.");
		return "redirect:/verify-otp";
	}

	@GetMapping("/verify-otp")
	String verifyForm(@RequestParam String email, Model model) {
		model.addAttribute("email", email);
		return "auth/verify-otp";
	}

	@PostMapping("/verify-otp")
	String verify(@RequestParam String email, @RequestParam String code, Model model) {
		OtpService.Result r = auth.verifyRegister(email, code);
		if (r == OtpService.Result.OK) {
			return "redirect:/login?verified=true";
		}
		model.addAttribute("email", email);
		model.addAttribute("error", otpError(r));
		return "auth/verify-otp";
	}

	@PostMapping("/register/resend-otp")
	String resend(@RequestParam String email, RedirectAttributes ra) {
		try {
			auth.resendRegisterOtp(email);
			ra.addFlashAttribute("success", "Đã gửi lại mã OTP.");
		} catch (BusinessException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		ra.addAttribute("email", email);
		return "redirect:/verify-otp";
	}

	// ---------- Quên mật khẩu ----------
	@GetMapping("/forgot-password")
	String forgotForm() {
		return "auth/forgot-password";
	}

	@PostMapping("/forgot-password")
	String forgot(@RequestParam String email, Model model, RedirectAttributes ra) {
		try {
			auth.forgotPassword(email);
		} catch (BusinessException e) {
			model.addAttribute("error", e.getMessage());
			return "auth/forgot-password";
		}
		ra.addAttribute("email", email.trim().toLowerCase());
		ra.addFlashAttribute("success", "Nếu email tồn tại, mã OTP đã được gửi.");
		return "redirect:/reset-password";
	}

	@GetMapping("/reset-password")
	String resetForm(@RequestParam String email, Model model) {
		ResetPasswordDTO dto = new ResetPasswordDTO();
		dto.setEmail(email);
		model.addAttribute("resetDTO", dto);
		return "auth/reset-password";
	}

	@PostMapping("/reset-password")
	String reset(@Valid @ModelAttribute("resetDTO") ResetPasswordDTO dto, BindingResult br, Model model) {
		if (!br.hasFieldErrors("newPassword") && !br.hasFieldErrors("confirmPassword")
				&& !dto.getNewPassword().equals(dto.getConfirmPassword())) {
			br.rejectValue("confirmPassword", "mismatch", "Mật khẩu xác nhận không khớp");
		}
		if (br.hasErrors()) {
			return "auth/reset-password";
		}
		OtpService.Result r = auth.resetPassword(dto);
		if (r == OtpService.Result.OK) {
			return "redirect:/login?reset=true";
		}
		model.addAttribute("error", otpError(r));
		return "auth/reset-password";
	}

	private String otpError(OtpService.Result r) {
		return switch (r) {
		case INVALID -> "Mã OTP không đúng.";
		case EXPIRED -> "Mã OTP đã hết hạn hoặc không tồn tại. Hãy yêu cầu mã mới.";
		case TOO_MANY_ATTEMPTS -> "Bạn đã nhập sai quá nhiều lần. Hãy yêu cầu mã mới.";
		case OK -> null;
		};
	}
}
