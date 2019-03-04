package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Minimum.class)
	private Long id;

	@NotNull
	@JsonIgnore
	private String weChatOpenId;

	@Column(length = 40)
	private String name;
}
