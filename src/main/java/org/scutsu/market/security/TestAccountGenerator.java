package org.scutsu.market.security;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.scutsu.market.models.User;
import org.scutsu.market.repositories.UserRepository;
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
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	public TestAccountGenerator(JwtTestUserProperties config, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
		this.config = config;
		this.userRepository = userRepository;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (!config.isEnabled()) {
			return;
		}

		User user = userRepository.findByWeChatOpenId(config.getWeChatOpenId());
		if (user == null) {
			User newUser = new User();
			newUser.setWeChatOpenId(config.getWeChatOpenId());
			user = userRepository.save(newUser);
		}
		Principal p = new Principal(user.getId(), "user");
		String jwt = jwtTokenProvider.generateToken(p);
		log.info("Test user ID: {} JWT: {}", user.getId(), jwt);
	}
}
