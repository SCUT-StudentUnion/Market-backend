package org.scutsu.market.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class User {

	@OneToMany(mappedBy = "releasedBy")
	private List<Goods> releasedGoods = new ArrayList<>();

	@ManyToMany
	private List<Goods> collectedGoods = new ArrayList<>();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(nullable = false)
	private String weChatOpenId;
}
