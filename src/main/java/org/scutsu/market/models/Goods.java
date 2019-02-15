package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Goods {

	@OneToMany
	@JsonView(Views.Goods.List.class)
	List<Photo> photos = new ArrayList<>();
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Minimum.class)
	private Long id;
	@NotNull
	@Column(length = 45)
	@JsonView({Views.Goods.List.class, Views.Goods.UserAccessible.class})
	private String title;
	@Column(length = 150)
	@JsonView({Views.Goods.List.class, Views.Goods.UserAccessible.class})
	private String detail;
	@ManyToOne
	@JsonView({Views.Goods.List.class, Views.Goods.UserAccessible.class})
	private Category category;

	@Column(precision = 12, scale = 2, nullable = true)
	@JsonView({Views.Goods.List.class, Views.Goods.UserAccessible.class})
	private BigDecimal buyingPrice;

	@Column(precision = 12, scale = 2)
	@JsonView({Views.Goods.List.class, Views.Goods.UserAccessible.class})
	private BigDecimal sellingPrice;

	@ManyToOne(optional = false)
	@JsonView(Views.Goods.List.class)
	private User releasedBy;

	@NotNull
	@Enumerated(EnumType.STRING)
	@JsonView(Views.Goods.List.class)
	private GoodsStatus status;

	@NotNull
	@JsonView(Views.Goods.List.class)
	private OffsetDateTime createdTime;

	@JsonView(Views.Goods.List.class)
	private OffsetDateTime onShelfTime;

	@NotNull
	@Enumerated(EnumType.STRING)
	@JsonView({Views.Goods.List.class, Views.Goods.UserAccessible.class})
	private GoodsAreaStatus area;

	@NotNull
	@Enumerated(EnumType.STRING)
	@JsonView({Views.Goods.List.class, Views.Goods.UserAccessible.class})
	private GoodsActiveStatus active;

	@NotNull
	@Column(length = 60)
	@JsonView({Views.Goods.List.class, Views.Goods.UserAccessible.class})
	private String contactInfo;

}
