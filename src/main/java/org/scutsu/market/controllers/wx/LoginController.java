package org.scutsu.market.controllers.wx;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
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

@Controller
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

	@RequestMapping(value = "/login")
	public Map<String, Object> OnLogin(@RequestBody Map<String, Object> reqMsg) throws IOException {

		String code = reqMsg.get("code").toString();
		String session_key = "";
		String openid = "";

		String result;
		try {
			URIBuilder builder = new URIBuilder("https://api.weixin.qq.com/sns/jscode2session");
			builder.setParameter("appid", config.getAppId())
				.setParameter("secret", config.getSecret())
				.setParameter("js_code", code)
				.setParameter("grant_type", config.getGrantType());

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(builder.build());
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonNode = mapper.readTree(result);
				session_key = jsonNode.get("session_key").textValue();
				openid = jsonNode.get("openid").textValue();

			}
		} catch (URISyntaxException e) {
			log.error("Unexpected error", e);
			throw new RuntimeException("Unexpected error", e);
		}

		if (!openid.equals("") && userRepository.findByWeChatOpenId(openid) == null) {
			User user = new User();
			user.setWeChatOpenId(openid);
			userRepository.save(user);
		}

		Long userId = userRepository.findByWeChatOpenId(openid).getId();
		String jwt = jwtTokenProvider.generateToken(userId);

		Map<String, Object> resMsg = new HashMap<>();
		resMsg.put("jwt", jwt);
		return resMsg;
	}
}
