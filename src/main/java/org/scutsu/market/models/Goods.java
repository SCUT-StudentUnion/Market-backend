package org.scutsu.market.models;

import org.scutsu.market.models.UserInfo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class Goods {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=45)
	private String title;
	
	@Column(length=150)
	private String detail; 
	
	@Column(nullable=false)
	private String tag;
	
	@OneToOne
	private UserInfo user;

	
}
