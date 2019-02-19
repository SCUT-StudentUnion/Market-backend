package org.scutsu.market.services;

import org.scutsu.market.ApiErrorCode;

@ApiErrorCode("we-chat-login-unknown")
public class WeChatLoginFailedException extends RuntimeException {
	WeChatLoginFailedException(String message) {
		super(message);
	}

	WeChatLoginFailedException(Throwable e) {
		super(e);
	}
}

@ApiErrorCode("we-chat-login-invalid-code")
class WeChatLoginInvalidCodeException extends WeChatLoginFailedException {
	WeChatLoginInvalidCodeException(String message) {
		super(message);
	}
}

@ApiErrorCode("we-chat-login-frequency-limited")
class WeChatLoginFrequencyLimitedException extends WeChatLoginFailedException {
	WeChatLoginFrequencyLimitedException(String message) {
		super(message);
	}
}
