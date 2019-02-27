package org.scutsu.market.models;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.net.URI;

@Data
@Entity
public class Photo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({Views.Minimum.class, Views.Goods.UserAccessible.class})
	private Long id;

	@NotNull
	private String fileName;

	@Transient
	@JsonView(Views.Goods.Public.class)
	private URI url;
}
