package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GoodsReviewStatus {
	@JsonProperty("pending") PENDING,
	@JsonProperty("approved") APPROVED,
	@JsonProperty("changeRequested") CHANGE_REQUESTED
}
