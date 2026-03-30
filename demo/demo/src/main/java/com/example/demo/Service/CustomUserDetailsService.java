package com.example.demo.Service;

import com.example.demo.Model.UserAccount;
import com.example.demo.Repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Bài 6 — đăng nhập từ CSDL: load {@link UserAccount} + {@link com.example.demo.Model.Role} làm authorities.
 * Tên role trong DB phải là ROLE_* để khớp {@code hasRole("ADMIN")} / {@code hasAnyRole("USER","ADMIN")}.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserAccountRepository userAccountRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserAccount account = userAccountRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));
		return User.builder()
				.username(account.getUsername())
				.password(account.getPassword())
				.disabled(!account.isEnabled())
				.authorities(account.getRoles().stream()
						.map(r -> new SimpleGrantedAuthority(r.getName()))
						.collect(Collectors.toList()))
				.build();
	}
}
