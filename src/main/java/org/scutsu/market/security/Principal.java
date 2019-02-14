package org.scutsu.market.security;

import lombok.Value;

import java.util.Collection;

@Value
public class Principal {
	Long userId;
	Collection<String> roles;
}
