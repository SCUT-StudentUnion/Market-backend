package org.scutsu.market.repositories;

import org.springframework.data.repository.CrudRepository;
import org.scutsu.market.models.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
