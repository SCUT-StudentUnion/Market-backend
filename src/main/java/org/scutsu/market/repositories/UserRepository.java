package org.scutsu.market.repositories;

import org.scutsu.market.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByWeChatOpenId(String openid);
}
