package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Data
public class Favorite {

	@EmbeddedId
	private PK pk = new PK();
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@MapsId("collectedById")
	private User collectedBy;
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	@MapsId("goodsId")
	@JsonView(Views.Goods.List.class)
	private Goods goods;
	@NotNull
	@JsonView(Views.Goods.List.class)
	private OffsetDateTime collectedTime;

	public void setCollectedBy(User user) {
		this.collectedBy = user;
		this.pk.collectedById = user.getId();
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
		this.pk.goodsId = goods.getId();
	}

	@Data
	@Embeddable
	public static class PK implements Serializable {

		private Long collectedById;

		private Long goodsId;
	}
}
