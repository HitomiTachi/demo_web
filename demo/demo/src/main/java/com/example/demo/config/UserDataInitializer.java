package com.example.demo.config;

import com.example.demo.Model.Role;
import com.example.demo.Model.UserAccount;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Seed tối thiểu: ROLE_ADMIN, ROLE_USER và hai tài khoản mẫu (BCrypt) — không hardcode trong SecurityConfig.
 * Đảm bảo có role đúng tên; chỉ tạo user nếu chưa tồn tại username.
 */
@Component
@RequiredArgsConstructor
public class UserDataInitializer implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final UserAccountRepository userAccountRepository;
	private final PasswordEncoder passwordEncoder;

	private Role ensureRole(String name) {
		return roleRepository.findByName(name).orElseGet(() -> {
			Role r = new Role();
			r.setName(name);
			return roleRepository.save(r);
		});
	}

	@Override
	public void run(String... args) {
		Role adminRole = ensureRole("ROLE_ADMIN");
		Role userRole = ensureRole("ROLE_USER");

		if (userAccountRepository.findByUsername("admin").isEmpty()) {
			UserAccount admin = new UserAccount();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin"));
			admin.setEnabled(true);
			admin.setRoles(Set.of(adminRole));
			userAccountRepository.save(admin);
		}

		if (userAccountRepository.findByUsername("user").isEmpty()) {
			UserAccount user = new UserAccount();
			user.setUsername("user");
			user.setPassword(passwordEncoder.encode("user"));
			user.setEnabled(true);
			user.setRoles(Set.of(userRole));
			userAccountRepository.save(user);
		}
	}
}
