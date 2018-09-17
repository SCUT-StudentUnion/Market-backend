package org.scutsu.market.repositories;

import org.scutsu.market.models.UserComment;
import org.springframework.data.repository.CrudRepository;

public interface UserCommentRepository extends CrudRepository<UserComment, Long> {

}
