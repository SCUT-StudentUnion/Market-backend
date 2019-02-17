package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Data
public class Goods {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Minimum.class)
	private Long id;

	@ManyToOne(optional = false)
	@JsonView(Views.Goods.List.class)
	private User releasedBy;

	@JsonView(Views.Goods.Public.class)
	private OffsetDateTime onShelfTime;

	@ManyToOne
	@JsonView(Views.Goods.List.class)
	private GoodsDescription currentDescription;

}
