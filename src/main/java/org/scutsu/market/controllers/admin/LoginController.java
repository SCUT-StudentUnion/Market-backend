package org.scutsu.market.controllers.admin;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

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
	public ResponseEntity login(String username, String password) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			String jwt = jwtTokenProvider.generateToken(new Principal(0L, Collections.singleton("admin")));
			return ResponseEntity.ok(new LoginResult(jwt));
		} catch (AuthenticationException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码不正确");
		}
	}
}
