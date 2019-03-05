package org.scutsu.market.services.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.scutsu.market.services.ApiClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties("we-chat.template-message")
@Data
class TemplateMessageProperties {
	@Data
	static class Template {
		@Nullable
		private String templateId;
		private Map<String, String> keywords;
	}

	private Map<String, Template> templates;
}

@Service
@Slf4j
@RequiredArgsConstructor
public class WeChatTemplateMessageService {

	private final AccessTokenProvider accessTokenProvider;
	private final ApiClient apiClient;
	private final TemplateMessageProperties properties;

	private boolean hasFormId(@Nullable String formId) {
		return StringUtils.hasText(formId) && !formId.equals("the formId is a mock one");
	}

	@Value
	@Builder
	private static class SendTemplateMessageBody {
		@JsonProperty("touser")
		String toUserOpenId;

		@JsonProperty("template_id")
		String templateId;

		@JsonProperty("form_id")
		String formId;

		Map<String, Keyword> data;

		@Value
		static class Keyword {
			String value;
		}
	}

	@Async
	public void sendReviewApproved(ReviewApprovedMessage message, @Nullable String formId, String openId) {
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		Map<String, String> messageMap = Map.of(
			"title", message.getTitle(),
			"remark", Optional.ofNullable(message.getRemark()).orElse("恭喜你，商品审核通过了，现在大家可以看到你发布的信息了"),
			"submitted-time", message.getSubmittedTime().format(formatter),
			"reviewed-time", message.getReviewedTime().format(formatter)
		);
		send("review-approved", messageMap, formId, openId);
	}

	private void send(String templateName, Map<String, String> message, @Nullable String formId, String openId) {
		if (!properties.getTemplates().containsKey(templateName)) {
			log.warn("template {} not configured", templateName);
			return;
		}
		if (!hasFormId(formId)) {
			log.debug("no valid formId found.");
			return;
		}

		var template = properties.getTemplates().get(templateName);

		SendTemplateMessageBody body = SendTemplateMessageBody.builder()
			.formId(formId)
			.templateId(template.getTemplateId())
			.toUserOpenId(openId)
			.data(message.entrySet().stream()
				.filter(m -> {
					if (!template.getKeywords().containsKey(m.getKey())) {
						log.warn("keyword {} not configured");
						return false;
					}
					return true;
				})
				.collect(Collectors.toMap(
					m -> template.getKeywords().get(m.getKey()),
					m -> new SendTemplateMessageBody.Keyword(m.getValue()))))
			.build();

		URI uri = UriComponentsBuilder.fromHttpUrl("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send")
			.queryParam("access_token", accessTokenProvider.getAccessToken())
			.build().toUri();

		ApiResult result = apiClient.post(uri, body, ApiResult.class);

		if (!result.isSuccess()) {
			switch (result.getErrorCode()) {
				case SendTemplateMessageErrorCode.FORM_ID_ALREADY_USED:
				case SendTemplateMessageErrorCode.FORM_ID_INVALID_OR_EXPIRED:
					log.info("Failed: {}", result.getErrorMessage());
					break;
				default:
					log.error("Failed: {}", result.getErrorMessage());
			}
		}
	}
}

class SendTemplateMessageErrorCode {
	static final int FORM_ID_INVALID_OR_EXPIRED = 41028,
		FORM_ID_ALREADY_USED = 41029,
		PAGE_INVALID = 41030,
		FREQUENCY_LIMITED = 45009;
}
