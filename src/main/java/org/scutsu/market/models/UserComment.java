package org.scutsu.market.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Entity
@Data
public class UserComment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Lob
	private String comment;

	@Column(nullable = false)
	private OffsetDateTime time;

	@NotNull
	@ManyToOne
	private Goods goods;

	@NotNull
	@ManyToOne
	private User user;
}
