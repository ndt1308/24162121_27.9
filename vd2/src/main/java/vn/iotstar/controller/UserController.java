package vn.iotstar.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.Role;
import vn.iotstar.exception.BusinessException;
import vn.iotstar.security.SecurityUtils;
import vn.iotstar.service.UserService;

/** Chỉ ADMIN (cấu hình trong SecurityConfig: /users/** hasRole ADMIN) */
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private static final int PAGE_SIZE = 5;

	private final UserService service;

	@ModelAttribute("roles")
	List<Role> roles() {
		return service.roles();
	}

	@GetMapping
	String list(@RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "0") int page,
			Model model) {
		Page<UserDTO> result = service.search(keyword, Math.max(page, 0), PAGE_SIZE);
		model.addAttribute("page", result);
		model.addAttribute("keyword", keyword);
		return "users/list";
	}

	@GetMapping("/new")
	String createForm(Model model) {
		model.addAttribute("user", new UserDTO());
		return "users/form";
	}

	@PostMapping
	String create(@Valid @ModelAttribute("user") UserDTO dto, BindingResult br, Model model,
			RedirectAttributes ra) {
		if (br.hasErrors()) {
			return "users/form";
		}
		try {
			service.create(dto);
		} catch (BusinessException e) {
			model.addAttribute("error", e.getMessage());
			return "users/form";
		}
		ra.addFlashAttribute("success", "Đã thêm người dùng.");
		return "redirect:/users";
	}

	@GetMapping("/{id}/edit")
	String editForm(@PathVariable Long id, Model model) {
		model.addAttribute("user", service.get(id));
		return "users/form";
	}

	@PostMapping("/{id}")
	String update(@PathVariable Long id, @Valid @ModelAttribute("user") UserDTO dto, BindingResult br,
			Model model, Authentication auth, RedirectAttributes ra) {
		dto.setId(id);
		if (br.hasErrors()) {
			return "users/form";
		}
		try {
			service.update(id, dto, SecurityUtils.email(auth));
		} catch (BusinessException e) {
			model.addAttribute("error", e.getMessage());
			return "users/form";
		}
		ra.addFlashAttribute("success", "Đã cập nhật người dùng.");
		return "redirect:/users";
	}

	@PostMapping("/{id}/delete")
	String delete(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
		try {
			service.delete(id, SecurityUtils.email(auth));
			ra.addFlashAttribute("success", "Đã xóa người dùng và các sản phẩm của họ.");
		} catch (BusinessException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/users";
	}
}
