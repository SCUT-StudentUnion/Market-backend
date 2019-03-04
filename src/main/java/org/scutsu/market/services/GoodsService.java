package org.scutsu.market.services;

import lombok.AllArgsConstructor;
import org.scutsu.market.models.Goods;
import org.scutsu.market.models.GoodsDescription;
import org.scutsu.market.models.GoodsReviewStatus;
import org.scutsu.market.models.User;
import org.scutsu.market.repositories.GoodsDescriptionRepository;
import org.scutsu.market.repositories.GoodsRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

@Service
@AllArgsConstructor
public class GoodsService {
	private final GoodsRepository goodsRepository;
	private final GoodsDescriptionRepository goodsDescriptionRepository;
	private final GoodsDescriptionDiffer goodsDescriptionDiffer;
	private final UserIdProvider userIdProvider;
	private final Clock clock;

	private void validate(@NonNull GoodsDescription desc) {
		Objects.requireNonNull(desc.getCategory());
		Objects.requireNonNull(desc.getCategory().getId());
	}

	@Transactional
	public Goods create(GoodsDescription desc) {
		validate(desc);

		desc.setReviewStatus(GoodsReviewStatus.PENDING);
		desc.setCreatedTime(OffsetDateTime.now(clock));
		Goods goods = new Goods();
		User releasedBy = new User();
		releasedBy.setId(userIdProvider.getCurrentUserId());
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
		if (!descDiff.isUpdated() && !goodsDescriptionRepository.existsByGoodsIdAndReviewStatusNot(goods.getId(), GoodsReviewStatus.APPROVED)) {
			return goods;
		}
		newDesc.setGoods(goods);
		newDesc.setCreatedTime(OffsetDateTime.now(clock));
		if (descDiff.isNeedReview()) {
			var previousChangeRequestedDesc =
				goodsDescriptionRepository.findByGoodsIdAndReviewStatus(goods.getId(), GoodsReviewStatus.CHANGE_REQUESTED);
			previousChangeRequestedDesc.ifPresent(d -> {
				newDesc.setReviewComments(d.getReviewComments());
				newDesc.setReviewedTime(d.getReviewedTime());
			});
			goodsDescriptionRepository.deleteByGoodsIdAndReviewStatusNot(goods.getId(), GoodsReviewStatus.APPROVED);
			newDesc.setReviewStatus(GoodsReviewStatus.PENDING);
			goodsDescriptionRepository.save(newDesc);
		} else {
			goodsDescriptionRepository.deleteByGoodsIdAndReviewStatusNot(goods.getId(), GoodsReviewStatus.APPROVED);
			newDesc.setReviewStatus(GoodsReviewStatus.APPROVED);
			var savedNewDesc = goodsDescriptionRepository.save(newDesc);
			goods.setCurrentDescription(savedNewDesc);
			goods = goodsRepository.save(goods);
			assert oldDesc != null;
			goodsDescriptionRepository.delete(oldDesc);
		}
		return goods;
	}

	@Transactional
	public void reviewApprove(GoodsDescription desc) {
		if (desc.getReviewStatus() == GoodsReviewStatus.APPROVED) {
			return;
		}
		desc.setReviewStatus(GoodsReviewStatus.APPROVED);
		desc.setReviewedTime(OffsetDateTime.now(clock));
		GoodsDescription oldDesc = desc.getGoods().getCurrentDescription();
		desc.getGoods().setCurrentDescription(desc);
		desc.getGoods().setOnShelfTime(OffsetDateTime.now(clock));
		goodsDescriptionRepository.save(desc);
		goodsRepository.save(desc.getGoods());
		if (oldDesc != null) {
			goodsDescriptionRepository.delete(oldDesc);
		}
	}

	@Transactional
	public void reviewRequestChange(GoodsDescription desc, String comments) {
		Goods goods = desc.getGoods();
		var oldDesc = goods.getCurrentDescription();
		if (oldDesc == desc) {
			// 下架该商品
			goods.setCurrentDescription(null);
			goodsRepository.save(goods);
			if (goodsDescriptionRepository.existsByGoodsIdAndReviewStatusNot(goods.getId(), GoodsReviewStatus.APPROVED)) {
				// 若希望下架产品，且用户已提交新的更改，则删除当前展示信息（保留用户提交的新版信息）
				goodsDescriptionRepository.delete(desc);
				return;
			}
		}
		desc.setReviewStatus(GoodsReviewStatus.CHANGE_REQUESTED);
		desc.setReviewComments(comments);
		desc.setReviewedTime(OffsetDateTime.now(clock));
		goodsDescriptionRepository.save(desc);
	}
}
