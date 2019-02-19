package org.scutsu.market.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.scutsu.market.models.User;
import org.scutsu.market.repositories.UserRepository;
import org.scutsu.market.security.JwtTokenProvider;
import org.scutsu.market.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ConfigurationProperties(prefix = "we-chat")
@Validated
@Component
@Data
class WeChatProperties {

	@NotNull
	private String appId;

	@NotNull
	private String secret;

	@NotNull
	private String grantType = "authorization_code";
}

class CodeToSessionErrorCode {
	static final int SUCCESS = 0,
		BUSY = -1,
		INVALID_CODE = 40029,
		FREQUENCY_LIMITED = 45011;
}

@Data
class CodeToSessionResult {

	@JsonProperty("openid")
	private final String openId;
	@JsonProperty("session_key")
	private final String sessionKey;
	@JsonProperty("unionid")
	private final String unionId;
	@JsonProperty("errcode")
	private final int errorCode;
	@JsonProperty("errmsg")
	private final String errorMessage;

	boolean isSuccess() {
		return errorCode == CodeToSessionErrorCode.SUCCESS;
	}
}

@Service
public class WeChatLoginService {

	private final JwtTokenProvider jwtTokenProvider;
	private final WeChatProperties config;
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;

	@Autowired
	public WeChatLoginService(JwtTokenProvider jwtTokenProvider, WeChatProperties config, ObjectMapper objectMapper, UserRepository userRepository) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.config = config;
		this.objectMapper = objectMapper;
		this.userRepository = userRepository;
	}

	public String loginOpenId(String openId) {
		User user = userRepository.findByWeChatOpenId(openId).orElseGet(() -> {
			User newUser = new User();
			newUser.setWeChatOpenId(openId);
			return userRepository.save(newUser);
		});

		Principal p = new Principal(user.getId(), "user");
		return jwtTokenProvider.generateToken(p);
	}

	/**
	 * @param code code from WeChat client
	 * @return jwt
	 * @throws WeChatLoginFailedException Cannot login with WeChat API
	 */
	public String loginCode(String code) {
		if (code == null || code.isEmpty()) {
			throw new IllegalArgumentException("code must not be empty");
		}

		var uri = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/sns/jscode2session")
			.queryParam("appid", config.getAppId())
			.queryParam("secret", config.getSecret())
			.queryParam("js_code", code)
			.queryParam("grant_type", config.getGrantType())
			.build().toUri();
		HttpRequest request = HttpRequest.newBuilder()
			.uri(uri)
			.build();
		HttpClient client = HttpClient.newHttpClient();

		try {
			var response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

			if (response.statusCode() != HttpStatus.OK.value()) {
				throw new WeChatLoginFailedException("jscode2session returned unexpected http status code " + response.statusCode());
			}

			CodeToSessionResult result = objectMapper.readValue(response.body(), CodeToSessionResult.class);
			if (!result.isSuccess()) {
				switch (result.getErrorCode()) {
					case CodeToSessionErrorCode.INVALID_CODE:
						throw new WeChatLoginInvalidCodeException(result.getErrorMessage());
					case CodeToSessionErrorCode.FREQUENCY_LIMITED:
						throw new WeChatLoginFrequencyLimitedException(result.getErrorMessage());
				}
				throw new WeChatLoginFailedException("jscode2session failed. code: " + result.getErrorCode() + " message: " + result.getErrorMessage());
			}

			return loginOpenId(result.getOpenId());
		} catch (IOException | InterruptedException e) {
			throw new WeChatLoginFailedException(e);
		}
	}
}
