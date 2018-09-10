package org.scutsu.market.models;

import org.scutsu.market.models.UserInfo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Entity
@Data
public class Commet {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private UserInfo user;
	
	@Column(length=210,nullable=false)
	private String Commet;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date date;
	
	
}
