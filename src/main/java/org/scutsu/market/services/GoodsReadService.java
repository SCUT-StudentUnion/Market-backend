package org.scutsu.market.services;

import lombok.AllArgsConstructor;
import org.scutsu.market.models.Goods;
import org.scutsu.market.models.GoodsDescription;
import org.scutsu.market.models.GoodsReviewStatus;
import org.scutsu.market.models.Photo;
import org.scutsu.market.repositories.GoodsDescriptionRepository;
import org.scutsu.market.repositories.GoodsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class GoodsReadService {
	private final GoodsRepository goodsRepository;
	private final GoodsDescriptionRepository goodsDescriptionRepository;
	private final PhotoUriGenerator photoUriGenerator;

	private void populatePhotoUri(Stream<GoodsDescription> descriptions) {
		descriptions.forEach(description -> {
			for (Photo photo : description.getPhotos()) {
				photo.setUrl(photoUriGenerator.generateUri(photo));
			}
		});
	}

	public Page<Goods> getAll(Pageable pageable) {
		var page = goodsRepository.findAllByCurrentDescriptionNotNull(pageable);
		var descriptions = page.get().map(Goods::getCurrentDescription);
		populatePhotoUri(descriptions);
		return page;
	}

	public Page<Goods> getAllByCategoryId(long categoryId, Pageable pageable) {
		var page = goodsRepository.findAllByCurrentDescriptionCategoryId(categoryId, pageable);
		var descriptions = page.get().map(Goods::getCurrentDescription);
		populatePhotoUri(descriptions);
		return page;
	}

	public Page<GoodsDescription> getAllNeedReview(Pageable pageable) {
		var page = goodsDescriptionRepository.findAllByReviewStatus(GoodsReviewStatus.PENDING, pageable);
		populatePhotoUri(page.get());
		return page;
	}

	public Page<GoodsDescription> getAllChangeRequested(Pageable pageable) {
		var page = goodsDescriptionRepository.findAllByReviewStatus(GoodsReviewStatus.CHANGE_REQUESTED, pageable);
		populatePhotoUri(page.get());
		return page;
	}
}
