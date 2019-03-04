package org.scutsu.market.services;

import lombok.AllArgsConstructor;
import org.scutsu.market.models.Favorite;
import org.scutsu.market.models.Goods;
import org.scutsu.market.models.User;
import org.scutsu.market.repositories.FavoriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final UserIdProvider userIdProvider;
	private final Clock clock;

	/**
	 * Add one goods multiple time will update collectedTime
	 */
	public void add(long goodsId) {
		var user = new User();
		user.setId(userIdProvider.getCurrentUserId());
		var goods = new Goods();
		goods.setId(goodsId);

		var favorite = new Favorite();
		favorite.setCollectedBy(user);
		favorite.setGoods(goods);
		favorite.setCollectedTime(OffsetDateTime.now(clock));
		favoriteRepository.save(favorite);
	}

	/**
	 * delete non-exists favorite will success
	 */
	@Transactional
	public void delete(long goodsId) {
		var pk = new Favorite.PK();
		pk.setCollectedById(userIdProvider.getCurrentUserId());
		pk.setGoodsId(goodsId);
		favoriteRepository.findById(pk).ifPresent(favoriteRepository::delete);
	}
}
