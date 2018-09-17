package org.scutsu.market.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class UserComment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 210, nullable = false)
	private String comment;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date date;

	@ManyToOne
	private Goods goods;

	@ManyToOne
	private User user;
}
