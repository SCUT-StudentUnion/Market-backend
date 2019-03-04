package org.scutsu.market.repositories;

import org.scutsu.market.models.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface FavoriteRepository extends CrudRepository<Favorite, Favorite.PK> {
	Page<Favorite> findAllByCollectedById(long userId, Pageable pageable);
}
