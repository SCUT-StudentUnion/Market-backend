package org.scutsu.market.models;

import lombok.Data;

import org.scutsu.market.models.Goods;

import javax.persistence.*;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class UserInfo {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	@Column(nullable=false)
	private String weixin_id;
	
	@NotNull
	@Column(nullable=false)
	private String tel_number;
	
	
}
