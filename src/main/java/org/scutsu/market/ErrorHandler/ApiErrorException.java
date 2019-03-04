package org.scutsu.market.ErrorHandler;

import lombok.Getter;

public class ApiErrorException extends RuntimeException implements ApiErrorCode {

	@Getter
	private final String errorCode;

	public ApiErrorException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ApiErrorException(String errorCode, Throwable e) {
		super(e);
		this.errorCode = errorCode;
	}
}
