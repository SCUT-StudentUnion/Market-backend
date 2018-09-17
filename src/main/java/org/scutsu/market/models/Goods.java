package org.scutsu.market.models;

import org.scutsu.market.models.User;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class Goods {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@Column(length=45)
	private String title;
	
	@NotNull
	@Column(length=150)
	private String detail; 
	
	@Column(nullable=false)
	private String tag;
	
	@Lob
	private Byte[] photo1;
	
	@Lob
	private Byte[] photo2;

	@NotNull
	@Column(length=24)
	private String BuyInPrice;
	
	@NotNull
	@Column(length=24)
	private String SailOutPrice;
	
	@ManyToOne
	private User user;
}
