package org.scutsu.market.repositories;

import org.scutsu.market.models.Photo;
import org.springframework.data.repository.CrudRepository;

public interface PhotoRepository extends CrudRepository<Photo, Long> {
	
	public Photo findByFileName(String fileName);
}
