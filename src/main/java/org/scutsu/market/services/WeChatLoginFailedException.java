package org.scutsu.market.services;

import org.scutsu.market.ErrorHandler.ApiErrorCodePrefix;
import org.scutsu.market.ErrorHandler.ApiErrorException;

@ApiErrorCodePrefix("we-chat")
public class WeChatLoginFailedException extends ApiErrorException {

	WeChatLoginFailedException(String message) {
		this("", message);
	}

	WeChatLoginFailedException(String errorCode, String message) {
		super(errorCode, message);
	}

	WeChatLoginFailedException(Throwable e) {
		super("", e);
	}
}
