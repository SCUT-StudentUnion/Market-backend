package org.scutsu.market.repositories;

import org.scutsu.market.models.Goods;
import org.scutsu.market.models.GoodsStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@RepositoryRestResource(path = "goods", collectionResourceRel = "goods")
public interface GoodsRepository extends CrudRepository<Goods, Long> {

	@Override
	@PreAuthorize("hasRole('USER') && #s.releasedBy.id == principal.userId || hasRole('ADMIN')")
	<S extends Goods> S save(S s);
}

@RepositoryEventHandler
@Component
class GoodsEventHandler {

	@HandleBeforeCreate
	public void handleGoodsCreate(Goods g) {
		g.setCreatedTime(OffsetDateTime.now());
		g.setStatus(GoodsStatus.PENDING_AUDIT);
	}

	@HandleBeforeSave
	public void handleGoodsSave(Goods g) {

	}
}
