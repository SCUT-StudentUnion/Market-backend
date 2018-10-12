package org.scutsu.market.security;

import io.jsonwebtoken.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.Duration;
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

@Component
@Slf4j
public class JwtTokenProvider {

	private final JwtProperties config;

	@Autowired
	public JwtTokenProvider(JwtProperties config) {
		this.config = config;
	}

	public String generateToken(Long userId) {

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + config.getExpiration().toMillis());

		return Jwts.builder()
			.setSubject(Long.toString(userId))
			.setIssuedAt(new Date())
			.setExpiration(expiryDate)
			.signWith(SignatureAlgorithm.HS512, config.getSecret())
			.compact();
	}

	Long getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser()
			.setSigningKey(config.getSecret())
			.parseClaimsJws(token)
			.getBody();

		return Long.parseLong(claims.getSubject());
	}

	boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(config.getSecret()).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			log.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
		}
		return false;
	}
}
