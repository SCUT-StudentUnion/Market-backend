package org.scutsu.market.services;

import lombok.AllArgsConstructor;
import org.scutsu.market.models.*;
import org.scutsu.market.repositories.FavoriteRepository;
import org.scutsu.market.repositories.GoodsDescriptionRepository;
import org.scutsu.market.repositories.GoodsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@AllArgsConstructor
public class GoodsReadService {
	private final GoodsRepository goodsRepository;
	private final GoodsDescriptionRepository goodsDescriptionRepository;
	private final FavoriteRepository favoriteRepository;
	private final PhotoUriGenerator photoUriGenerator;
	private final UserIdProvider userIdProvider;

	private void populatePhotoUri(@NonNull GoodsDescription description) {
		for (Photo photo : description.getPhotos()) {
			photo.setUrl(photoUriGenerator.generateUri(photo));
		}
	}

	private void populatePhotoUri(@NonNull Streamable<GoodsDescription> descriptions) {
		descriptions.forEach(this::populatePhotoUri);
	}

	private void populateGoodsPhotoUri(@NonNull Streamable<Goods> goods) {
		goods.stream().map(Goods::getCurrentDescription).filter(Objects::nonNull).forEach(this::populatePhotoUri);
	}

	public Page<Goods> getAll(Pageable pageable) {
		var page = goodsRepository.findAllByCurrentDescriptionNotNull(pageable);
		populateGoodsPhotoUri(page);
		return page;
	}

	public Page<Goods> getAllByCategoryId(long categoryId, Pageable pageable) {
		var page = goodsRepository.findAllByCurrentDescriptionCategoryId(categoryId, pageable);
		populateGoodsPhotoUri(page);
		return page;
	}

	public Page<GoodsDescription> getAllNeedReview(Pageable pageable) {
		var page = goodsDescriptionRepository.findAllByReviewStatus(GoodsReviewStatus.PENDING, pageable);
		populatePhotoUri(page);
		return page;
	}

	public Page<GoodsDescription> getAllChangeRequested(Pageable pageable) {
		var page = goodsDescriptionRepository.findAllByReviewStatus(GoodsReviewStatus.CHANGE_REQUESTED, pageable);
		populatePhotoUri(page);
		return page;
	}

	public Goods get(long goodsId) {
		var goods = goodsRepository.findByIdAndCurrentDescriptionNotNull(goodsId).orElseThrow();

		assert goods.getCurrentDescription() != null;
		populatePhotoUri(goods.getCurrentDescription());
		return goods;
	}

	@Transactional(readOnly = true)
	public Page<Favorite> getFavorite(Pageable pageable) {
		var page = favoriteRepository.findAllByCollectedById(userIdProvider.getCurrentUserId(), pageable);
		populateGoodsPhotoUri(page.map(Favorite::getGoods));
		return page;
	}

	@Transactional(readOnly = true)
	public Page<GoodsDescription> getMy(Pageable pageable) {
		var page = goodsDescriptionRepository.findAllByGoodsReleasedById(userIdProvider.getCurrentUserId(), pageable);
		populatePhotoUri(page);
		return page;
	}

	public GoodsDescription getMy(long descriptionId) {
		var desc = goodsDescriptionRepository.findByIdAndGoodsReleasedById(descriptionId, userIdProvider.getCurrentUserId()).orElseThrow();
		populatePhotoUri(desc);
		return desc;
	}
}
