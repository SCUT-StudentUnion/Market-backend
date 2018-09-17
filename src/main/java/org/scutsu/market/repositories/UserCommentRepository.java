package org.scutsu.market.repositories;

import org.springframework.data.repository.CrudRepository;
import org.scutsu.market.models.UserComment;

public interface UserCommentRepository extends CrudRepository<UserComment, Long> {

}
