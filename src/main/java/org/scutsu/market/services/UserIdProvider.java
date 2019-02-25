package org.scutsu.market.services;

import org.scutsu.market.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserIdProvider {
	public long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Principal p = (Principal) authentication.getPrincipal();
		return p.getUserId();
	}
}
