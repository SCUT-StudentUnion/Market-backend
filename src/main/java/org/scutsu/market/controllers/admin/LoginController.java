package org.scutsu.market.controllers.admin;

import lombok.Data;
import org.scutsu.market.models.DTOs.LoginResult;
import org.scutsu.market.security.JwtTokenProvider;
import org.scutsu.market.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminLoginController")
@RequestMapping("/admin")
public class LoginController {
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	public LoginController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity login(@RequestBody LoginFormDTO loginForm) {
		try {
			authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));
			String jwt = jwtTokenProvider.generateToken(new Principal(0L, "admin"));
			return ResponseEntity.ok(new LoginResult(jwt));
		} catch (AuthenticationException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码不正确");
		}
	}

	@Data
	private static class LoginFormDTO {
		private String username, password;
	}
}
