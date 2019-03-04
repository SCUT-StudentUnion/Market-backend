package org.scutsu.market.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.scutsu.market.models.GoodsDescription;
import org.scutsu.market.models.Photo;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 判断商品描述是否的确有更新，是否更新了需要审核的内容，以减少不必要的审核。
 */
@Service
public class GoodsDescriptionDiffer {

	public Diff checkDiff(@Nullable GoodsDescription oldDesc, @Nonnull GoodsDescription newDesc) {
		if (oldDesc == null) {
			return Diff.NEED_REVIEW;
		}
		List<Function<GoodsDescription, Object>> needReview = List.of(
			GoodsDescription::getTitle,
			GoodsDescription::getDetail,
			GoodsDescription::getContactInfo,
			d -> d.getPhotos().stream().map(Photo::getId).collect(Collectors.toSet()));
		List<Function<GoodsDescription, Object>> updates = List.of(
			GoodsDescription::getBuyingPrice,
			GoodsDescription::getSellingPrice,
			GoodsDescription::getArea,
			GoodsDescription::getActive,
			d -> d.getCategory() == null ? null : d.getCategory().getId());
		for (var a : needReview) {
			if (!Objects.equals(a.apply(oldDesc), a.apply(newDesc))) {
				return Diff.NEED_REVIEW;
			}
		}
		for (var a : updates) {
			if (!Objects.equals(a.apply(oldDesc), a.apply(newDesc))) {
				return Diff.UPDATED;
			}
		}
		return Diff.NO_CHANGE;
	}

	@Getter
	@AllArgsConstructor
	public enum Diff {
		NEED_REVIEW(true, true),
		UPDATED(true, false),
		NO_CHANGE(false, false);

		private final boolean updated;
		private final boolean needReview;
	}
}
