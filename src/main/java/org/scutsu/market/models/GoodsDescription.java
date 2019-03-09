package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 本类表示商品发布者一次提交的商品信息，用于{@link Goods}中。该实体对于用户来说是不可变的，即每次更新商品信息时都创建一个新的
 * 实体，而不是修改现有实体。
 *
 * <p>
 * 将商品发布者提交的信息单独分离为一个实体有以下好处：
 * <ul>
 * <li>同一个商品可以同时拥有多个版本的描述信息。目前最多两个版本，一个版本用于线上展示，一个提交管理员审核。</li>
 * <li>
 * 审核操作均针对该实体完成（而非{@link Goods}）。该实体对用户不可变可以保证：管理员在审核时看到的描述和他同意上线的版本是一样的。
 * </li>
 * </ul>
 * </p>
 *
 * @see Goods
 */
@Entity
@Data
public class GoodsDescription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Minimum.class)
	private Long id;

	@ManyToOne(optional = false)
	@JsonView(Views.Goods.Self.class)
	private Goods goods;

	@ManyToMany(fetch = FetchType.EAGER)
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

	@JsonView(Views.Goods.Self.class)
	private OffsetDateTime reviewedTime;

	@JsonView(Views.Goods.Self.class)
	private GoodsReviewStatus reviewStatus;

	@Lob
	@JsonView(Views.Goods.Self.class)
	private String reviewComment;

	@Nullable
	@JsonView(Views.Goods.UserAccessible.class)
	private String weChatFormId;
}
