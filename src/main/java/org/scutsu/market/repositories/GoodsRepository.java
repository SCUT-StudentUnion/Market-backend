package org.scutsu.market.repositories;

import org.scutsu.market.models.Goods;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GoodsRepository extends CrudRepository<Goods, Long> {

	@Override
	@PreAuthorize("hasRole('USER') && #s.releasedBy.id == principal.userId || hasRole('ADMIN')")
	<S extends Goods> S save(S s);
}
