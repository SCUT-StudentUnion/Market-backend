package org.scutsu.market.models;

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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(length = 45)
	private String title;

	@Column(length = 150)
	private String detail;

	@OneToMany
	List<Photo> photos = new ArrayList<>();

	@ManyToOne
	private Category category;

	@Column(precision = 12, scale = 2, nullable = true)
	private BigDecimal buyingPrice;

	@Column(precision = 12, scale = 2)
	private BigDecimal sellingPrice;

	@ManyToOne(optional = false)
	private User releasedBy;

	@NotNull
	@Enumerated(EnumType.STRING)
	private GoodsStatus status;

	@NotNull
	private OffsetDateTime createdTime;

	private OffsetDateTime onShelfTime;

	@NotNull
	@Enumerated(EnumType.STRING)
	private GoodsAreaStatus area;

	@NotNull
	@Enumerated(EnumType.STRING)
	private GoodsActiveStatus active;

	@NotNull
	@Column(length = 60)
	private String contactInfo;

}
