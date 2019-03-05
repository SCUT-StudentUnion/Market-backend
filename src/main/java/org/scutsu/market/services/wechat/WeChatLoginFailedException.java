package org.scutsu.market.services.wechat;

import org.scutsu.market.ErrorHandler.ApiErrorCodePrefix;
import org.scutsu.market.ErrorHandler.ApiErrorException;

@ApiErrorCodePrefix("we-chat.login")
class WeChatLoginFailedException extends ApiErrorException {

	WeChatLoginFailedException(String errorCode, String message) {
		super(errorCode, message);
	}

	WeChatLoginFailedException(Throwable e) {
		super("", e);
	}
}
