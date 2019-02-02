package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GoodsActiveStatus {
	
	@JsonProperty("sell")
	SELL,
	@JsonProperty("buy")
	BUY
}
