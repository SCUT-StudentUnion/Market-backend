package org.scutsu.market.controllers.admin;

import lombok.Data;
import org.scutsu.market.ErrorHandler.ApiErrorCodePrefix;
import org.scutsu.market.models.DTOs.LoginResult;
import org.scutsu.market.security.JwtTokenProvider;
import org.scutsu.market.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
	public LoginResult login(@RequestBody @Valid LoginFormDTO loginForm) {
		try {
			authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));
			String jwt = jwtTokenProvider.generateToken(new Principal(0L, "admin"));
			return new LoginResult(jwt);
		} catch (BadCredentialsException e) {
			throw new AdminBadCredentialsException(e);
		}
	}

	@Data
	private static class LoginFormDTO {
		@NotNull
		private String username;
		@NotNull
		private String password;
	}

	@ApiErrorCodePrefix("admin-login.bad-credentials")
	class AdminBadCredentialsException extends RuntimeException {
		AdminBadCredentialsException(Throwable e) {
			super("用户名或密码错误", e);
		}
	}
}
