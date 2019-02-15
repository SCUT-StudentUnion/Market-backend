package org.scutsu.market.services;

import org.scutsu.market.models.Goods;
import org.scutsu.market.models.GoodsStatus;
import org.scutsu.market.models.User;
import org.scutsu.market.repositories.GoodsRepository;
import org.scutsu.market.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Objects;

@Component
public class GoodsService {
	private final GoodsRepository goodsRepository;

	@Autowired
	public GoodsService(GoodsRepository goodsRepository) {
		this.goodsRepository = goodsRepository;
	}

	private void validate(Goods goods) {
		Objects.requireNonNull(goods.getCategory());
		Objects.requireNonNull(goods.getCategory().getId());
	}

	public Goods create(Goods goods) {
		validate(goods);

		goods.setStatus(GoodsStatus.PENDING_AUDIT);
		goods.setCreatedTime(OffsetDateTime.now());
		User releasedBy = new User();
		releasedBy.setId(getCurrentUserId());
		goods.setReleasedBy(releasedBy);

		return goodsRepository.save(goods);
	}

	public Goods update(Goods goods) {
		validate(goods);
		return goodsRepository.save(goods);
	}

	public void auditPass(Goods goods) {
		if (goods.getStatus() == GoodsStatus.SELLING) {
			return;
		}
		goods.setStatus(GoodsStatus.SELLING);
		goods.setOnShelfTime(OffsetDateTime.now());
		goodsRepository.save(goods);
	}

	public Iterable<Goods> getAll() {
		return goodsRepository.findAll();
	}

	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Principal p = (Principal) authentication.getPrincipal();
		return p.getUserId();
	}
}
