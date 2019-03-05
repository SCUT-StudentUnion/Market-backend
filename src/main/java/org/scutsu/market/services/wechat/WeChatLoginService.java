package org.scutsu.market.services.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.scutsu.market.ErrorHandler.ApiErrorException;
import org.scutsu.market.models.User;
import org.scutsu.market.repositories.UserRepository;
import org.scutsu.market.security.JwtTokenProvider;
import org.scutsu.market.security.Principal;
import org.scutsu.market.services.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "we-chat")
@Validated
@Component
@Data
class WeChatProperties {

	@NotNull
	private String appId;

	@NotNull
	private String secret;
}

class CodeToSessionErrorCode {
	static final int INVALID_CODE = 40029,
		FREQUENCY_LIMITED = 45011;
}

@Data
@EqualsAndHashCode(callSuper = true)
class CodeToSessionResult extends ApiResult {

	@JsonProperty("openid")
	private String openId;
	@JsonProperty("session_key")
	private String sessionKey;
	@JsonProperty("unionid")
	private String unionId;
}

@Service
public class WeChatLoginService {

	private final JwtTokenProvider jwtTokenProvider;
	private final WeChatProperties config;
	private final ApiClient apiClient;
	private final UserRepository userRepository;

	@Autowired
	public WeChatLoginService(JwtTokenProvider jwtTokenProvider, WeChatProperties config, ApiClient apiClient, UserRepository userRepository) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.config = config;
		this.apiClient = apiClient;
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
			.queryParam("grant_type", "authorization_code")
			.build().toUri();

		try {
			CodeToSessionResult result = apiClient.get(uri, CodeToSessionResult.class);
			if (!result.isSuccess()) {
				String errorCode = result.getErrorCode() == CodeToSessionErrorCode.INVALID_CODE ? "invalid-code"
					: result.getErrorCode() == CodeToSessionErrorCode.FREQUENCY_LIMITED ? "frequency-limited"
					: "";
				throw new WeChatLoginFailedException(errorCode, "jscode2session failed. code: " + result.getErrorCode() + " message: " + result.getErrorMessage());
			}

			return loginOpenId(result.getOpenId());
		} catch (ApiErrorException e) {
			throw new WeChatLoginFailedException(e);
		}
	}
}
