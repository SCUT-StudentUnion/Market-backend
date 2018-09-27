package org.scutsu.market.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class User {
	@OneToMany(mappedBy = "releasedBy")
	List<Goods> releasedGoods = new ArrayList<>();
	@ManyToMany
	List<Goods> collectedGoods = new ArrayList<>();
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotNull
	@Column(nullable = false)
	private String weChatOpenId;
	@NotNull
	@Column(nullable = false)
	private String telephoneNumber;

	public List<Goods> getReleasedGoods() {
		return releasedGoods;
	}

	public void setReleasedGoods(List<Goods> releasedGoods) {
		this.releasedGoods = releasedGoods;
	}

	public List<Goods> getCollectedGoods() {
		return collectedGoods;
	}

	public void setCollectedGoods(List<Goods> collectedGoods) {
		this.collectedGoods = collectedGoods;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWeChatOpenId() {
		return weChatOpenId;
	}

	public void setWeChatOpenId(String weChatOpenId) {
		this.weChatOpenId = weChatOpenId;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
}
