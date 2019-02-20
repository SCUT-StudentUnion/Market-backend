package org.scutsu.market.repositories;

import org.scutsu.market.models.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface GoodsRepository extends CrudRepository<Goods, Long> {
	Page<Goods> findAllByCurrentDescriptionNotNull(Pageable pageable);
}
