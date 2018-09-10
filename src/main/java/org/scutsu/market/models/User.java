package org.scutsu.market.models;

import lombok.Data;

import org.scutsu.market.models.Goods;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.persistence.OneToMany;

@Entity
@Data
public class User extends UserInfo {
	
	@OneToMany
	private List<Goods> UserReleaseGoodsList=new ArrayList<>();
	
	@OneToMany
	private List<Goods> UserCollectGoodsList=new ArrayList<>();
	
}
