package org.scutsu.market.repositories;

import org.scutsu.market.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findByWeChatOpenId(String openid);
}
