package org.scutsu.market.services;

import org.scutsu.market.models.Goods;
import org.scutsu.market.models.GoodsDescription;
import org.scutsu.market.models.GoodsReviewStatus;
import org.scutsu.market.models.User;
import org.scutsu.market.repositories.GoodsDescriptionRepository;
import org.scutsu.market.repositories.GoodsRepository;
import org.scutsu.market.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class GoodsService {
	private final GoodsRepository goodsRepository;
	private final GoodsDescriptionRepository goodsDescriptionRepository;
	private final GoodsDescriptionDiffer goodsDescriptionDiffer;

	@Autowired
	public GoodsService(GoodsRepository goodsRepository, GoodsDescriptionRepository goodsDescriptionRepository, GoodsDescriptionDiffer goodsDescriptionDiffer) {
		this.goodsRepository = goodsRepository;
		this.goodsDescriptionRepository = goodsDescriptionRepository;
		this.goodsDescriptionDiffer = goodsDescriptionDiffer;
	}

	private void validate(GoodsDescription desc) {
		Objects.requireNonNull(desc.getCategory());
		Objects.requireNonNull(desc.getCategory().getId());
	}

	@Transactional
	public Goods create(GoodsDescription desc) {
		validate(desc);

		desc.setReviewStatus(GoodsReviewStatus.PENDING);
		desc.setCreatedTime(OffsetDateTime.now());
		Goods goods = new Goods();
		User releasedBy = new User();
		releasedBy.setId(getCurrentUserId());
		goods.setReleasedBy(releasedBy);

		goods = goodsRepository.save(goods);
		desc.setGoods(goods);
		goodsDescriptionRepository.save(desc);
		return goods;
	}

	@Transactional
	public Goods update(Goods goods, GoodsDescription newDesc) {
		validate(newDesc);
		GoodsDescription oldDesc = goods.getCurrentDescription();
		GoodsDescriptionDiffer.Diff descDiff = goodsDescriptionDiffer.checkDiff(oldDesc, newDesc);
		if (!descDiff.isUpdated()) {
			return goods;
		}
		newDesc.setGoods(goods);
		newDesc.setCreatedTime(OffsetDateTime.now());
		if (descDiff.isNeedReview()) {
			newDesc.setReviewStatus(GoodsReviewStatus.PENDING);
			goodsDescriptionRepository.save(newDesc);
			return goods;
		} else {
			newDesc.setReviewStatus(GoodsReviewStatus.APPROVED);
			newDesc = goodsDescriptionRepository.save(newDesc);
			goods.setCurrentDescription(newDesc);
			return goodsRepository.save(goods);
		}
	}

	@Transactional
	public void reviewApprove(GoodsDescription desc) {
		if (desc.getReviewStatus() == GoodsReviewStatus.APPROVED) {
			return;
		}
		desc.setReviewStatus(GoodsReviewStatus.APPROVED);
		desc.setReviewedTime(OffsetDateTime.now());
		GoodsDescription oldDesc = desc.getGoods().getCurrentDescription();
		desc.getGoods().setCurrentDescription(desc);
		desc.getGoods().setOnShelfTime(OffsetDateTime.now());
		goodsDescriptionRepository.save(desc);
		goodsRepository.save(desc.getGoods());
		if (oldDesc != null) {
			goodsDescriptionRepository.delete(oldDesc);
		}
	}

	@Transactional
	public void reviewRequestChange(GoodsDescription desc, String comments) {
		desc.setReviewStatus(GoodsReviewStatus.CHANGE_REQUESTED);
		desc.setReviewComments(comments);
		desc.setReviewedTime(OffsetDateTime.now());
		desc = goodsDescriptionRepository.save(desc);
		Goods goods = desc.getGoods();
		if (goods.getCurrentDescription() != null) {
			goods.setCurrentDescription(null);
			goodsRepository.save(goods);
		}
	}

	public Page<Goods> getAll(Pageable pageable) {
		return goodsRepository.findAllByCurrentDescriptionNotNull(pageable);
	}

	public Page<GoodsDescription> getAllNeedReview(Pageable pageable) {
		return goodsDescriptionRepository.findAllByReviewStatus(GoodsReviewStatus.PENDING, pageable);
	}

	public Page<GoodsDescription> getAllChangeRequested(Pageable pageable) {
		return goodsDescriptionRepository.findAllByReviewStatus(GoodsReviewStatus.CHANGE_REQUESTED, pageable);
	}

	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Principal p = (Principal) authentication.getPrincipal();
		return p.getUserId();
	}
}
