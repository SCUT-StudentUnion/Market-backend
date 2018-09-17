package org.scutsu.market.models;

import lombok.Data;

import org.scutsu.market.models.Goods;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class User {
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	@Column(nullable=false)
	private String WeChatOpenId;
	
	@NotNull
	@Column(nullable=false)
	private String TelephoneNumber;
	
	@OneToMany
	List<Goods> UserReleaseGoodsList=new ArrayList<>();
	
	@OneToMany
	List<Goods> UserCollectGoodsList=new ArrayList<>();
	
	public void setWeChatOpenId(String s) {
		this.WeChatOpenId=s;
	}

	public void setTelephoneNumber(String s) {
		this.TelephoneNumber=s;
	}

}
