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
public class GoodsDescription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Minimum.class)
	private Long id;

	@ManyToOne(optional = false)
	@JsonView(Views.Goods.Admin.class)
	private Goods goods;

	@OneToMany(fetch = FetchType.EAGER)
	@JsonView({Views.Goods.Public.class, Views.Goods.UserAccessible.class})
	private List<Photo> photos = new ArrayList<>();

	@NotNull
	@Column(length = 45)
	@JsonView({Views.Goods.Public.class, Views.Goods.UserAccessible.class})
	private String title;

	@Column(length = 150)
	@JsonView({Views.Goods.Public.class, Views.Goods.UserAccessible.class})
	private String detail;

	@ManyToOne
	@JsonView({Views.Goods.Public.class, Views.Goods.UserAccessible.class})
	private Category category;

	@Column(precision = 12, scale = 2)
	@JsonView({Views.Goods.Public.class, Views.Goods.UserAccessible.class})
	private BigDecimal buyingPrice;

	@Column(precision = 12, scale = 2)
	@JsonView({Views.Goods.Public.class, Views.Goods.UserAccessible.class})
	private BigDecimal sellingPrice;

	@NotNull
	@JsonView({Views.Goods.Public.class, Views.Goods.UserAccessible.class})
	private GoodsAreaStatus area;

	@NotNull
	@JsonView({Views.Goods.Public.class, Views.Goods.UserAccessible.class})
	private GoodsActiveStatus active;

	@NotNull
	@Column(length = 60)
	@JsonView({Views.Goods.Public.class, Views.Goods.UserAccessible.class})
	private String contactInfo;

	@NotNull
	@JsonView(Views.Goods.Public.class)
	private OffsetDateTime createdTime;

	@JsonView(Views.Goods.Admin.class)
	private OffsetDateTime reviewedTime;

	@JsonView(Views.Goods.Admin.class)
	private GoodsReviewStatus reviewStatus;

	@Lob
	@JsonView(Views.Goods.Admin.class)
	private String reviewComments;
}
