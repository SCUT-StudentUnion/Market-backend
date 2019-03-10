package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

/**
 * 商品信息由两部分构成，本类以及{@link GoodsDescription}
 *
 * <p>
 * 本类代表一个用户发布的商品，负责保存不会被商品发布者更改的商品信息。
 * 收藏、发布、评论等用户与商品之间发生的关系也有本类处理。
 * </p>
 *
 * @see GoodsDescription
 */
@Entity
@Data
@NamedEntityGraph(name = "Goods.publicList",
	attributeNodes = {
		@NamedAttributeNode(value = "currentDescription", subgraph = "description"),
		@NamedAttributeNode("releasedBy")
	},
	subgraphs = @NamedSubgraph(name = "description", attributeNodes = {
		@NamedAttributeNode("category"),
	}))
@NamedEntityGraph(name = "Goods.publicDetail",
	attributeNodes = {
		@NamedAttributeNode(value = "currentDescription", subgraph = "description"),
		@NamedAttributeNode("releasedBy")
	},
	subgraphs = @NamedSubgraph(name = "description", attributeNodes = {
		@NamedAttributeNode("category"),
		@NamedAttributeNode("photos"),
	}))
@NamedEntityGraph(name = "Goods.forUpdate",
	attributeNodes = {
		@NamedAttributeNode(value = "currentDescription", subgraph = "description"),
	},
	subgraphs = @NamedSubgraph(name = "description", attributeNodes = {
		@NamedAttributeNode("category"),
		@NamedAttributeNode("photos"),
	}))
public class Goods {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Minimum.class)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonView({Views.Goods.List.class, Views.Goods.Detail.class})
	@NotNull
	private User releasedBy;

	/**
	 * 商品上架展示的时间
	 */
	@JsonView(Views.Goods.Public.class)
	private OffsetDateTime onShelfTime;

	/**
	 * 当前向公众展示的描述。
	 * <p>为{@code null}表示该商品不对公众展示。</p>
	 */
	@ManyToOne
	@JsonView({Views.Goods.List.class, Views.Goods.Detail.class})
	@Nullable
	private GoodsDescription currentDescription;

	@JsonView(Views.Goods.Self.class)
	public boolean isPublished() {
		return currentDescription != null;
	}
}
