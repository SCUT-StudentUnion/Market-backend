package org.scutsu.market.controllers.wechat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "we-chat")
@Validated
@Component
@Data
public class WeChatProperties {

	@NotNull
	private String appId;

	@NotNull
	private String secret;

	@NotNull
	private String loginURL;

	@NotNull
	private String logoutURL;

	@NotNull
	private String loginSuccess;

	@NotNull
	private String loginError;

	@NotNull
	private String grantType = "authorization_code";
}

