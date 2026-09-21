package vn.iotstar.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.exception.BusinessException;
import vn.iotstar.security.SecurityUtils;
import vn.iotstar.service.ProductService;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	private static final int PAGE_SIZE = 5;

	private final ProductService service;

	private static boolean isAdmin(Authentication auth) {
		return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	}

	@GetMapping
	String list(@RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "0") int page,
			Authentication auth, Model model) {
		Page<ProductDTO> result = service.search(keyword, Math.max(page, 0), PAGE_SIZE, SecurityUtils.email(auth),
				isAdmin(auth));
		model.addAttribute("page", result);
		model.addAttribute("keyword", keyword);
		return "products/list";
	}

	@GetMapping("/new")
	String createForm(Model model) {
		model.addAttribute("product", new ProductDTO());
		return "products/form";
	}

	@PostMapping
	String create(@Valid @ModelAttribute("product") ProductDTO dto, BindingResult br,
			@RequestParam(value = "file", required = false) MultipartFile file, Authentication auth, Model model,
			RedirectAttributes ra) {
		if (br.hasErrors()) {
			return "products/form";
		}
		try {
			service.create(dto, file, SecurityUtils.email(auth));
		} catch (BusinessException e) {
			model.addAttribute("error", e.getMessage());
			return "products/form";
		}
		ra.addFlashAttribute("success", "Đã thêm sản phẩm.");
		return "redirect:/products";
	}

	@GetMapping("/{id}/edit")
	String editForm(@PathVariable Long id, Authentication auth, Model model) {
		model.addAttribute("product", service.get(id, SecurityUtils.email(auth), isAdmin(auth)));
		return "products/form";
	}

	@PostMapping("/{id}")
	String update(@PathVariable Long id, @Valid @ModelAttribute("product") ProductDTO dto, BindingResult br,
			@RequestParam(value = "file", required = false) MultipartFile file, Authentication auth, Model model,
			RedirectAttributes ra) {
		dto.setId(id);
		if (br.hasErrors()) {
			return "products/form";
		}
		try {
			service.update(id, dto, file, SecurityUtils.email(auth), isAdmin(auth));
		} catch (BusinessException e) {
			model.addAttribute("error", e.getMessage());
			return "products/form";
		}
		ra.addFlashAttribute("success", "Đã cập nhật sản phẩm.");
		return "redirect:/products";
	}

	@PostMapping("/{id}/delete")
	String delete(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
		try {
			service.delete(id, SecurityUtils.email(auth), isAdmin(auth));
			ra.addFlashAttribute("success", "Đã xóa sản phẩm.");
		} catch (BusinessException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/products";
	}
}
