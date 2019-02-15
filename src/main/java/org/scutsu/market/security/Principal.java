package org.scutsu.market.security;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.Collection;

@Value
@AllArgsConstructor
public class Principal {
	Long userId;
	Collection<String> roles;

	public Principal(Long userId, String... roles) {
		this.userId = userId;
		this.roles = Arrays.asList(roles);
	}
}
