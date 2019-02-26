package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class Photo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({Views.Minimum.class, Views.Goods.UserAccessible.class})
	private Long id;

	@NotNull
	@JsonView(Views.Goods.List.class)
	private String fileName;
}
