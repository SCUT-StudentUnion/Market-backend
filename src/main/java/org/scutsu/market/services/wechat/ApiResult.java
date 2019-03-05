package org.scutsu.market.services.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

class ApiErrorCode {
	static final int SUCCESS = 0,
		BUSY = -1;
}

@Data
public class ApiResult {
	@JsonProperty("errcode")
	private int errorCode;
	@JsonProperty("errmsg")
	private String errorMessage;

	boolean isSuccess() {
		return errorCode == ApiErrorCode.SUCCESS;
	}
}
