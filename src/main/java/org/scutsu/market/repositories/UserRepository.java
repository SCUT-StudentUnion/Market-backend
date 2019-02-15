package org.scutsu.market.repositories;

import org.scutsu.market.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;

public interface UserRepository extends CrudRepository<User, Long> {

	@Nullable
	User findByWeChatOpenId(String openid);
}
