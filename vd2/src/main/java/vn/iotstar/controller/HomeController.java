package vn.iotstar.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import vn.iotstar.security.SecurityUtils;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.UserService;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final UserService userService;
	private final ProductService productService;

	@GetMapping("/")
	String home() {
		return "home";
	}

	@GetMapping("/dashboard")
	String dashboard(Authentication auth, Model model) {
		model.addAttribute("totalUsers", userService.countAll());
		model.addAttribute("totalProducts", productService.countAll());
		model.addAttribute("myProducts", productService.countOf(SecurityUtils.email(auth)));
		return "dashboard";
	}

	@GetMapping("/access-denied")
	String accessDenied() {
		return "access-denied";
	}
}
