package org.scutsu.market.repositories;

import org.scutsu.market.models.Goods;
import org.scutsu.market.models.GoodsInlineCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection = GoodsInlineCategory.class, path = "goods", collectionResourceRel = "goods")
public interface GoodsRepository extends CrudRepository<Goods, Long> {

}
