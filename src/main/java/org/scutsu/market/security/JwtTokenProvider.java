package org.scutsu.market.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.scutsu.market.ApiErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;

@Data
@Validated
@Component
@ConfigurationProperties(prefix = "jwt")
class JwtProperties {

	@NotNull
	private String secret;

	@NotNull
	private Duration expiration = Duration.ofDays(2);
}

@ApiErrorCode("invalid-jwt")
@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidJwtException extends RuntimeException {
	InvalidJwtException(Throwable e) {
		super(e);
	}
}

@Component
@Slf4j
public class JwtTokenProvider {

	private final JwtProperties config;

	@Autowired
	public JwtTokenProvider(JwtProperties config) {
		this.config = config;
	}

	public String generateToken(Principal principal) {

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + config.getExpiration().toMillis());
		Claims claims = Jwts.claims()
			.setSubject(Long.toString(principal.getUserId()))
			.setIssuedAt(new Date())
			.setExpiration(expiryDate);
		claims.put("roles", String.join(" ", principal.getRoles()));
		return Jwts.builder()
			.setClaims(claims)
			.signWith(SignatureAlgorithm.HS512, config.getSecret())
			.compact();
	}

	Principal getPrincipalFromJWT(String token) {
		try {
			Claims claims = Jwts.parser()
				.setSigningKey(config.getSecret())
				.parseClaimsJws(token)
				.getBody();
			String roles = (String) claims.getOrDefault("roles", "user");
			return new Principal(
				Long.parseLong(claims.getSubject()),
				Arrays.asList(roles.split(" ")));
		} catch (JwtException e) {
			throw new InvalidJwtException(e);
		}
	}
}
