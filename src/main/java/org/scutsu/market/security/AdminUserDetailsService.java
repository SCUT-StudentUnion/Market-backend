package org.scutsu.market.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@Component
@ConfigurationProperties(prefix = "admin.login")
class AdminLoginProperties {

	@NotNull
	private String username = "admin";

	@NotNull
	private String password;
}

@Service
public class AdminUserDetailsService extends InMemoryUserDetailsManager {

	@Autowired
	public AdminUserDetailsService(AdminLoginProperties adminLoginProperties) {
		this.createUser(User.withUsername(adminLoginProperties.getUsername())
			.password("{noop}" + adminLoginProperties.getPassword())
			.roles("ADMIN")
			.build());
	}
}
