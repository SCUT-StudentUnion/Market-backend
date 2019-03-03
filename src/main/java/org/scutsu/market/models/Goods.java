package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Entity
@Data
public class Goods {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Minimum.class)
	private Long id;

	@ManyToOne
	@JsonView(Views.Goods.Public.class)
	@NotNull
	private User releasedBy;

	@JsonView(Views.Goods.Public.class)
	private OffsetDateTime onShelfTime;

	@ManyToOne
	@JsonView({Views.Goods.List.class, Views.Goods.Detail.class})
	@Nullable
	private GoodsDescription currentDescription;

}
