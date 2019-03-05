package org.scutsu.market.services.wechat;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ReviewApprovedMessage {
	String title;
	String remark;
	LocalDateTime submittedTime;
	LocalDateTime reviewedTime;
}
