package org.scutsu.market.security;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.scutsu.market.services.wechat.WeChatLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt.test-user")
class JwtTestUserProperties {
	/**
	 * Enable generating test user.
	 * JWT of test user will be written to log.
	 */
	private boolean enabled = false;
	private String weChatOpenId = "test-open-id";
}

@Component
@Slf4j
public class TestAccountGenerator implements ApplicationRunner {

	private final JwtTestUserProperties config;
	private final WeChatLoginService weChatLoginService;

	@Autowired
	public TestAccountGenerator(JwtTestUserProperties config, WeChatLoginService weChatLoginService) {
		this.config = config;
		this.weChatLoginService = weChatLoginService;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (!config.isEnabled()) {
			return;
		}

		var jwt = weChatLoginService.loginOpenId(config.getWeChatOpenId());
		log.info("Test user JWT: {}", jwt);
	}
}
