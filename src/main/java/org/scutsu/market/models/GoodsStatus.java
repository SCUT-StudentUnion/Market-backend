package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GoodsStatus {

	@JsonProperty("selling")
	SELLING,
	@JsonProperty("soldOut")
	SOLD_OUT,
	@JsonProperty("pendingAudit")
	PENDING_AUDIT,
	@JsonProperty("auditFailed")
	AUDIT_FAILED,
	@JsonProperty("offShelf")
	OFF_SHELF
}
