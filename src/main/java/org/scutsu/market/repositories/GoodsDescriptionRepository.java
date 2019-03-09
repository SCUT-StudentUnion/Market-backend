package org.scutsu.market.repositories;

import org.scutsu.market.models.GoodsDescription;
import org.scutsu.market.models.GoodsReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GoodsDescriptionRepository extends CrudRepository<GoodsDescription, Long> {
	Page<GoodsDescription> findAllByReviewStatus(GoodsReviewStatus reviewStatus, Pageable pageable);

	Page<GoodsDescription> findAllByGoodsReleasedById(long userId, Pageable pageable);

	Optional<GoodsDescription> findByGoodsIdAndReviewStatus(long goodsId, GoodsReviewStatus reviewStatus);

	Optional<GoodsDescription> findByIdAndGoodsReleasedById(long id, long releasedById);

	void deleteByGoodsIdAndReviewStatusNot(long goodsId, GoodsReviewStatus reviewStatus);

	boolean existsByGoodsIdAndReviewStatusNot(long goodsId, GoodsReviewStatus reviewStatus);
}
