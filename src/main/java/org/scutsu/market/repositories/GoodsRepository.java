package org.scutsu.market.repositories;

import org.scutsu.market.models.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GoodsRepository extends CrudRepository<Goods, Long> {
	Page<Goods> findAllByCurrentDescriptionNotNull(Pageable pageable);

	Page<Goods> findAllByCurrentDescriptionCategoryId(long id, Pageable pageable);

	Optional<Goods> findByIdAndCurrentDescriptionNotNull(Long id);
}
