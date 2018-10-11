package org.scutsu.market.repositories;

import org.scutsu.market.models.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}
