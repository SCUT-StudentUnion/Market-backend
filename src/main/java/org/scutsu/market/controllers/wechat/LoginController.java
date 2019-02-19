package org.scutsu.market.controllers.wechat;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.scutsu.market.models.DTOs.LoginResult;
import org.scutsu.market.services.WeChatLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/wechat")
@Slf4j
public class LoginController {

	private final WeChatLoginService weChatLoginService;

	@Autowired
	public LoginController(WeChatLoginService weChatLoginService) {
		this.weChatLoginService = weChatLoginService;
	}

	@PostMapping("/login")
	public LoginResult OnLogin(@RequestBody @Valid LoginForm form) {

		var jwt = weChatLoginService.loginCode(form.getCode());
		return new LoginResult(jwt);
	}

	@Data
	private static class LoginForm {
		@NotBlank
		private String code;
	}
}
