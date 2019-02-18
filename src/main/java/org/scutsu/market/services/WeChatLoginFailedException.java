package org.scutsu.market.services;

public class WeChatLoginFailedException extends RuntimeException {
	WeChatLoginFailedException(String message) {
		super(message);
	}
}
