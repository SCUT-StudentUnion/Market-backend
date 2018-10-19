package org.scutsu.market.models;

import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Projection(name = "inlineCategoryPhoto", types = {Goods.class})
public interface GoodsInlineCategory {

	String getTitle();

	String getDetail();

	List<Photo> getPhotos();

	Category getCategory();

	BigDecimal getBuyingPrice();

	BigDecimal getSellingPrice();

	GoodsStatus getStatus();

	OffsetDateTime getCreatedTime();

	OffsetDateTime getOnShelfTime();
}
