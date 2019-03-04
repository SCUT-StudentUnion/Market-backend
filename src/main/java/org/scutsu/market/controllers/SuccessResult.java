package org.scutsu.market.controllers;

import lombok.Value;

@Value
class SuccessResult {
	String message;

	SuccessResult() {
		message = "OK";
	}
}
