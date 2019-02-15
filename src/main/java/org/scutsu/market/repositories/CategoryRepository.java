package org.scutsu.market.repositories;

import org.scutsu.market.models.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@PreAuthorize("hasRole('ADMIN')")
public interface CategoryRepository extends CrudRepository<Category, Long> {
	@Override
	@PreAuthorize("permitAll")
	Iterable<Category> findAll();

	@Override
	@PreAuthorize("permitAll")
	Optional<Category> findById(Long aLong);
}
