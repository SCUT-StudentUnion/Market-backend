package org.scutsu.market.services.wechat;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ReviewChangeRequestedMessage {
	String title;
	String comment;
	String remark;
	LocalDateTime submittedTime;
	LocalDateTime reviewedTime;
}
