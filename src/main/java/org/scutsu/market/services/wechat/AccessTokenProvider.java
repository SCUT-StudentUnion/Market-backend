package org.scutsu.market.services.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.scutsu.market.services.ApiClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executor;

@Data
@ConfigurationProperties("we-chat.access-token")
@Component
class AccessTokenProviderProperties {
	private Duration refreshBeforeExpire = Duration.ofMinutes(30);
	private Duration forceRefreshBeforeExpire = Duration.ofMinutes(1);
}

@Data
class GetAccessTokenResult {

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("expires_in")
	private Integer expiresIn;
}

@Service
@Scope("singleton")
@Slf4j
public class AccessTokenProvider {

	private final Executor executor;
	private final AccessTokenProviderProperties properties;
	private final WeChatProperties weChatProperties;
	private final ApiClient apiClient;

	public AccessTokenProvider(@Qualifier("taskExecutor") Executor executor, AccessTokenProviderProperties properties, WeChatProperties weChatProperties, ApiClient apiClient) {
		this.executor = executor;
		this.properties = properties;
		this.weChatProperties = weChatProperties;
		this.apiClient = apiClient;
	}

	private String accessToken;
	private Instant expireTime = Instant.MIN;
	private boolean refreshing = false;

	public String getAccessToken() {
		var now = Instant.now();
		if (now.plus(properties.getForceRefreshBeforeExpire()).isAfter(expireTime)) {
			return forceRefreshAccessToken();
		}
		if (!refreshing && now.plus(properties.getRefreshBeforeExpire()).isAfter(expireTime)) {
			executor.execute(this::forceRefreshAccessToken);
		}
		return accessToken;
	}

	public String forceRefreshAccessToken() {
		refreshing = true;
		try {
			var uri = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/cgi-bin/token")
				.queryParam("appid", weChatProperties.getAppId())
				.queryParam("secret", weChatProperties.getSecret())
				.queryParam("grant_type", "client_credential")
				.build().toUri();

			GetAccessTokenResult result = apiClient.get(uri, GetAccessTokenResult.class);
			this.accessToken = result.getAccessToken();
			this.expireTime = Instant.now().plusSeconds(result.getExpiresIn());
			log.info("New access token acquired");
		} finally {
			refreshing = false;
		}
		return this.accessToken;
	}
}
