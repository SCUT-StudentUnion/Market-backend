package org.scutsu.market.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.scutsu.market.models.GoodsDescription;
import org.scutsu.market.models.Photo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GoodsDescriptionDiffer {

	public Diff checkDiff(GoodsDescription oldDesc, GoodsDescription newDesc) {
		List<Accessor> needReview = Arrays.asList(
			GoodsDescription::getTitle,
			GoodsDescription::getDetail,
			GoodsDescription::getContactInfo,
			d -> d.getPhotos().stream().map(Photo::getId).collect(Collectors.toSet()));
		List<Accessor> updates = Arrays.asList(
			GoodsDescription::getBuyingPrice,
			GoodsDescription::getSellingPrice,
			GoodsDescription::getArea,
			GoodsDescription::getActive,
			d -> d.getCategory() == null ? null : d.getCategory().getId());
		for (Accessor a : needReview) {
			if (!Objects.equals(a.access(oldDesc), a.access(newDesc))) {
				return Diff.NEED_REVIEW;
			}
		}
		for (Accessor a : updates) {
			if (!Objects.equals(a.access(oldDesc), a.access(newDesc))) {
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

	interface Accessor {
		Object access(GoodsDescription desc);
	}
}
