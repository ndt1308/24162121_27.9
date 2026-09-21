package vn.iotstar.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import vn.iotstar.security.CustomUserDetails;

@Controller
public class AuthController {

	// POST /login do Spring Security tự xử lý
	@GetMapping("/login")
	String login(@AuthenticationPrincipal CustomUserDetails me) {
		return me != null ? "redirect:/" : "auth/login";
	}
}
