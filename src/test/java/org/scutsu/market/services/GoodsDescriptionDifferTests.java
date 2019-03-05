package org.scutsu.market.services;

import org.junit.Test;
import org.scutsu.market.models.Category;
import org.scutsu.market.models.GoodsDescription;
import org.scutsu.market.models.Photo;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * @see GoodsDescriptionDiffer
 */
public class GoodsDescriptionDifferTests {

	private final GoodsDescriptionDiffer goodsDescriptionDiffer;

	public GoodsDescriptionDifferTests() {
		this.goodsDescriptionDiffer = new GoodsDescriptionDiffer();
	}

	@Test
	public void needReview() {
		GoodsDescription base = new GoodsDescription();
		GoodsDescription working = new GoodsDescription();
		base.setTitle("test goods");
		working.setTitle("test goods modified");
		GoodsDescriptionDiffer.Diff diff = goodsDescriptionDiffer.checkDiff(base, working);

		assertEquals(GoodsDescriptionDiffer.Diff.NEED_REVIEW, diff);
	}

	@Test
	public void changedButNotNeedReview() {
		GoodsDescription base = new GoodsDescription();
		GoodsDescription working = new GoodsDescription();
		base.setTitle("test goods");
		base.setSellingPrice(BigDecimal.valueOf(10.0));
		working.setTitle("test goods");
		base.setSellingPrice(BigDecimal.valueOf(11.0));

		GoodsDescriptionDiffer.Diff diff = goodsDescriptionDiffer.checkDiff(base, working);

		assertEquals(GoodsDescriptionDiffer.Diff.UPDATED, diff);
	}

	@Test
	public void irrelevant() {
		GoodsDescription base = new GoodsDescription();
		GoodsDescription working = new GoodsDescription();
		base.setTitle("test goods");
		base.setSellingPrice(BigDecimal.valueOf(10.0));
		base.setId(136L);
		working.setTitle("test goods");
		working.setSellingPrice(BigDecimal.valueOf(10.0));
		working.setId(0L);

		GoodsDescriptionDiffer.Diff diff = goodsDescriptionDiffer.checkDiff(base, working);

		assertEquals(GoodsDescriptionDiffer.Diff.NO_CHANGE, diff);
	}

	@Test
	public void noChanges() {
		GoodsDescription base = new GoodsDescription();
		GoodsDescription working = new GoodsDescription();
		base.setTitle("test goods");
		base.setSellingPrice(BigDecimal.valueOf(10.0));
		base.setId(0L);
		working.setTitle("test goods");
		working.setSellingPrice(BigDecimal.valueOf(10.0));
		working.setId(0L);

		GoodsDescriptionDiffer.Diff diff = goodsDescriptionDiffer.checkDiff(base, working);

		assertEquals(GoodsDescriptionDiffer.Diff.NO_CHANGE, diff);
	}

	@Test
	public void subObject() {
		GoodsDescription base = new GoodsDescription();
		GoodsDescription working = new GoodsDescription();
		base.setCategory(new Category());
		base.getCategory().setId(10L);
		base.getCategory().setName("test category");
		working.setCategory(new Category());
		working.getCategory().setId(10L);

		GoodsDescriptionDiffer.Diff diff1 = goodsDescriptionDiffer.checkDiff(base, working);

		assertFalse(diff1.isUpdated());

		working.getCategory().setId(11L);
		GoodsDescriptionDiffer.Diff diff2 = goodsDescriptionDiffer.checkDiff(base, working);
		assertTrue(diff2.isUpdated());
	}

	@Test
	public void list() {
		GoodsDescription base = new GoodsDescription();
		GoodsDescription working = new GoodsDescription();
		base.getPhotos().add(new Photo());
		base.getPhotos().get(0).setId(10L);
		base.getPhotos().get(0).setFileName("test-photo.jpg");
		base.getPhotos().add(new Photo());
		base.getPhotos().get(1).setId(11L);
		base.getPhotos().get(1).setFileName("test-photo2.jpg");

		working.getPhotos().add(new Photo());
		working.getPhotos().get(0).setId(10L);
		working.getPhotos().add(new Photo());
		working.getPhotos().get(1).setId(11L);

		GoodsDescriptionDiffer.Diff diff1 = goodsDescriptionDiffer.checkDiff(base, working);

		assertFalse(diff1.isUpdated());

		working.getPhotos().get(1).setId(12L);
		GoodsDescriptionDiffer.Diff diff2 = goodsDescriptionDiffer.checkDiff(base, working);
		assertTrue(diff2.isUpdated());

		working.getPhotos().remove(1);
		GoodsDescriptionDiffer.Diff diff3 = goodsDescriptionDiffer.checkDiff(base, working);
		assertTrue(diff3.isUpdated());
	}
}
