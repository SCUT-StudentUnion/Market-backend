package org.scutsu.market.controllers.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.scutsu.market.models.User;
import org.scutsu.market.repositories.UserRepository;
import org.scutsu.market.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

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

@Data
class LoginResult {

	private final String jwt;
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
	@JsonProperty("errMsg")
	private final String errorMessage;

	boolean isSuccess() {
		return errorCode == 0;
	}
}

@RestController
@RequestMapping("/wechat")
@Slf4j
public class LoginController {

	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final WeChatProperties config;

	@Autowired
	public LoginController(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, WeChatProperties config) {
		this.userRepository = userRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.config = config;
	}

	@PostMapping("/login")
	public LoginResult OnLogin(@RequestBody Map<String, String> reqMsg) throws IOException {

		String code = reqMsg.get("code");

		try {
			URIBuilder builder = new URIBuilder("https://api.weixin.qq.com/sns/jscode2session");
			builder.setParameter("appid", config.getAppId())
				.setParameter("secret", config.getSecret())
				.setParameter("js_code", code)
				.setParameter("grant_type", config.getGrantType());

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(builder.build());
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				throw new RuntimeException("jscode2session returned unexpected code " + statusCode);
			}

			String resultTxt = EntityUtils.toString(response.getEntity(), "utf-8");
			ObjectMapper mapper = new ObjectMapper();
			CodeToSessionResult result = mapper.readValue(resultTxt, CodeToSessionResult.class);
			if (!result.isSuccess()) {
				throw new RuntimeException("jscode2session failed. code: " + result.getErrorCode() + " message: " + result.getErrorMessage());
			}

			User user = userRepository.findByWeChatOpenId(result.getOpenId());
			if (user == null) {
				user = new User();
				user.setWeChatOpenId(result.getOpenId());
				userRepository.save(user);
			}
			String jwt = jwtTokenProvider.generateToken(user.getId());
			return new LoginResult(jwt);

		} catch (URISyntaxException e) {
			throw new RuntimeException("Unexpected error", e); // should never happen
		}
	}
}
