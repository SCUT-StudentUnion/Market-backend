package org.scutsu.market.repositories;

import org.scutsu.market.models.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GoodsRepository extends CrudRepository<Goods, Long> {
	@EntityGraph("Goods.publicList")
	Page<Goods> findAllByCurrentDescriptionNotNull(Pageable pageable);

	@EntityGraph("Goods.publicList")
	Page<Goods> findAllByCurrentDescriptionCategoryId(long id, Pageable pageable);

	@EntityGraph("Goods.publicDetail")
	Optional<Goods> findByIdAndCurrentDescriptionNotNull(long id);

	@EntityGraph("Goods.forUpdate")
	Optional<Goods> findForUpdateByIdAndReleasedById(long id, long userId);
}
