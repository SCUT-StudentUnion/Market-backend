package org.scutsu.market.repositories;

import org.scutsu.market.models.Goods;
import org.springframework.data.repository.CrudRepository;

public interface GoodsRepository extends CrudRepository<Goods, Long> {
	Iterable<Goods> findAllByCurrentDescriptionNotNull();
}
