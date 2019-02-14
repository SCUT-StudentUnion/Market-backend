package org.scutsu.market.repositories;

import org.scutsu.market.models.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

@PreAuthorize("hasAuthority('admin')")
public interface CategoryRepository extends CrudRepository<Category, Long> {
	@Override
	@PreAuthorize("true")
	Iterable<Category> findAll();

	@Override
	@PreAuthorize("true")
	Optional<Category> findById(Long aLong);
}
