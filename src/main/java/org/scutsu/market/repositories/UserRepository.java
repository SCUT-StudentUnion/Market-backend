package org.scutsu.market.repositories;

import java.util.List;

import org.scutsu.market.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	public List<User> findByWeChatOpenId(String openid);
}
