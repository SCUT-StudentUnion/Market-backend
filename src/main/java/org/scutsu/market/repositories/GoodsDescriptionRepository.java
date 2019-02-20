package org.scutsu.market.repositories;

import org.scutsu.market.models.GoodsDescription;
import org.scutsu.market.models.GoodsReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface GoodsDescriptionRepository extends CrudRepository<GoodsDescription, Long> {
	Page<GoodsDescription> findAllByReviewStatus(GoodsReviewStatus reviewStatus, Pageable pageable);
}
