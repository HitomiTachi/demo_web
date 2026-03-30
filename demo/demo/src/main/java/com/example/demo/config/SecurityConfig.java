package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Bài 6 — PasswordEncoder (BCrypt), SecurityFilterChain, formLogin, logout.
 * {@link com.example.demo.Service.CustomUserDetailsService} load user từ DB (không dùng InMemoryUserDetailsManager).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/login", "/error", "/css/**", "/images/**", "/favicon.ico").permitAll()
						.requestMatchers(HttpMethod.GET, "/", "/books").permitAll()
						.requestMatchers("/products/add", "/products/edit/**", "/products/delete/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/products/search").hasAnyRole("USER", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/products").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/cart/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/checkout").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/order/success").authenticated()
						.requestMatchers("/books/**").hasRole("ADMIN")
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/products", true)
						.permitAll()
				)
				.logout(logout -> logout
						.logoutSuccessUrl("/login?logout")
						.permitAll()
				);
		return http.build();
	}
}
