package org.scutsu.market.repositories;

import org.springframework.data.repository.CrudRepository;
import org.scutsu.market.models.UserInfo;

public interface UserInfoRepository extends CrudRepository<UserInfo, Long> {

}
