package vn.iotstar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/** Xác thực bằng CustomUserDetailsService (username hoặc email) + BCrypt. */
	@Bean
	DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	/** Tài khoản chưa xác thực OTP (enabled=false) -> /login?disabled=true */
	@Bean
	AuthenticationFailureHandler loginFailureHandler() {
		return (request, response, ex) -> {
			String target = (ex instanceof DisabledException) ? "/login?disabled=true" : "/login?error=true";
			response.sendRedirect(request.getContextPath() + target);
		};
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/login", "/register", "/register/resend-otp", "/verify-otp",
						"/forgot-password", "/reset-password",
						"/css/**", "/js/**", "/images/**", "/error").permitAll()
				.requestMatchers("/users/**").hasRole("ADMIN")
				.anyRequest().authenticated())
			.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.usernameParameter("username") // ô nhập username HOẶC email
				.passwordParameter("password")
				.defaultSuccessUrl("/dashboard", true)
				.failureHandler(loginFailureHandler())
				.permitAll())
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout=true")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.permitAll())
			.exceptionHandling(ex -> ex.accessDeniedPage("/access-denied"));
		return http.build();
	}
}
