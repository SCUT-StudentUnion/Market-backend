package org.scutsu.market.security;

import org.scutsu.market.security.CustomUserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scutsu.market.security.JwtTokenProvider;

public class JWTAuthenticationFilter extends OncePerRequestFilter{
	
	private CustomUserDetailsService customUserDetailsService;
	
	private JwtTokenProvider tokenProvider;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) {
		try {
			String jwt=getJwtFromRequest(request);
			
			if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
				Long userId = tokenProvider.getUserIdFromJWT(jwt);
				UserDetails userDetail=customUserDetailsService.loadUserByUserId(userId);
				
			}
			
			
		}
		catch() {
			
		}
	}
	
	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken=request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
		return null;
	}
}
