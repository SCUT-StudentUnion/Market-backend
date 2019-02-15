package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({Views.Minimum.class, Views.Goods.UserAccessible.class})
	private Long id;

	@NotNull
	@Column(length = 45)
	@JsonView(Views.Goods.List.class)
	private String name;
}
