package org.scutsu.market.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.scutsu.market.models.Category;
import org.scutsu.market.models.Goods;
import org.scutsu.market.models.GoodsDescription;
import org.scutsu.market.models.GoodsReviewStatus;
import org.scutsu.market.repositories.GoodsDescriptionRepository;
import org.scutsu.market.repositories.GoodsRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @see GoodsService
 */
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class GoodsServiceTest {

	private static final long currentUserId = 1268L;
	private static final String reviewComment = "This is my comment";
	private static final OffsetDateTime lastReviewTime = OffsetDateTime.parse("2007-12-03T10:15:30+08:00");
	@Mock
	private GoodsRepository goodsRepository;
	@Mock
	private GoodsDescriptionRepository goodsDescriptionRepository;
	@Mock
	private GoodsDescriptionDiffer goodsDescriptionDiffer;
	@Mock
	private UserIdProvider userIdProvider;
	private GoodsService goodsService;
	private Clock clock;

	@Before
	public void init() {
		when(userIdProvider.getCurrentUserId()).thenReturn(currentUserId);
		when(goodsDescriptionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
		when(goodsRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
		goodsService = new GoodsService(goodsRepository, goodsDescriptionRepository, goodsDescriptionDiffer, userIdProvider, clock);
	}

	private void assertOffShelf(Goods goods, GoodsDescription desc) {
		assertSame(goods, desc.getGoods());
		assertNull(goods.getCurrentDescription());
	}

	private void assertPendingOffShelf(Goods goods, GoodsDescription desc) {
		assertOffShelf(goods, desc);
		assertEquals(GoodsReviewStatus.PENDING, desc.getReviewStatus());
		assertEquals(OffsetDateTime.now(clock), desc.getCreatedTime());
	}

	private void assertChangeRequestedOffShelf(Goods goods, GoodsDescription desc) {
		assertOffShelf(goods, desc);
		assertEquals(GoodsReviewStatus.CHANGE_REQUESTED, desc.getReviewStatus());
		assertEquals(OffsetDateTime.now(clock), desc.getReviewedTime());
		assertEquals(GoodsServiceTest.reviewComment, desc.getReviewComment());
	}

	private void assertPendingOnShelf(Goods goods, GoodsDescription desc) {
		assertSame(goods, desc.getGoods());
		assertNotNull(goods.getCurrentDescription());
		assertEquals(GoodsReviewStatus.APPROVED, goods.getCurrentDescription().getReviewStatus());
		assertEquals(GoodsReviewStatus.PENDING, desc.getReviewStatus());
		assertEquals(OffsetDateTime.now(clock), desc.getCreatedTime());
	}

	private void assertPublished(Goods goods, GoodsDescription desc) {
		assertSame(goods, desc.getGoods());
		assertSame(goods.getCurrentDescription(), desc);
		assertEquals(GoodsReviewStatus.APPROVED, desc.getReviewStatus());
	}

	private GoodsDescription buildDescription() {
		var desc = new GoodsDescription();
		desc.setCategory(new Category());
		desc.getCategory().setId(589L);
		return desc;
	}

	private Goods buildOnShelfGoods() {
		var goods = new Goods();
		goods.setId(123L);
		var oldDesc = new GoodsDescription();
		oldDesc.setReviewStatus(GoodsReviewStatus.APPROVED);
		oldDesc.setGoods(goods);
		goods.setCurrentDescription(oldDesc);
		return goods;
	}

	@Test
	public void create() {
		var newDesc = buildDescription();

		var newGoods = goodsService.create(newDesc);

		var inOrder = inOrder(goodsDescriptionRepository, goodsRepository);
		inOrder.verify(goodsRepository).save(newGoods);
		inOrder.verify(goodsDescriptionRepository).save(newDesc);
		assertPendingOffShelf(newGoods, newDesc);
		assertEquals(currentUserId, (long) newGoods.getReleasedBy().getId());
	}

	private void assertReviewInfoCopied(GoodsDescription desc) {
		assertEquals(reviewComment, desc.getReviewComment());
		assertEquals(lastReviewTime, desc.getReviewedTime());
	}

	@Test
	public void updateOffShelf() {
		var goods = new Goods();
		goods.setId(123L);
		var newDesc = buildDescription();
		when(goodsDescriptionDiffer.checkDiff(null, newDesc)).thenReturn(GoodsDescriptionDiffer.Diff.NEED_REVIEW);
		when(goodsDescriptionRepository.findByGoodsIdAndReviewStatus(goods.getId(), GoodsReviewStatus.CHANGE_REQUESTED))
			.thenReturn(Optional.of(buildChangeRequestedDescription()));
		when(goodsRepository.findForUpdateByIdAndReleasedById(goods.getId(), currentUserId)).thenReturn(Optional.of(goods));
		var savedGoods = goodsService.update(goods.getId(), newDesc);

		var inOrder = inOrder(goodsDescriptionRepository, goodsRepository);
		inOrder.verify(goodsDescriptionRepository).deleteByGoodsIdAndReviewStatusNot(goods.getId(), GoodsReviewStatus.APPROVED);
		inOrder.verify(goodsDescriptionRepository).save(newDesc);
		assertPendingOffShelf(savedGoods, newDesc);
		assertReviewInfoCopied(newDesc);
	}

	private void verifyReplace(Goods goods, GoodsDescription oldDesc, GoodsDescription newDesc) {
		var inOrder = inOrder(goodsDescriptionRepository, goodsRepository);
		verifyReplace(inOrder, goods, oldDesc, newDesc);
	}

	private void verifyReplace(InOrder inOrder, Goods goods, GoodsDescription oldDesc, GoodsDescription newDesc) {
		inOrder.verify(goodsDescriptionRepository).save(newDesc);
		inOrder.verify(goodsRepository).save(goods);
		inOrder.verify(goodsDescriptionRepository).delete(oldDesc);
	}

	@Test
	public void updatePublishedNoChange() {
		var goods = buildOnShelfGoods();
		var oldDesc = goods.getCurrentDescription();
		assert oldDesc != null;
		var newDesc = buildDescription();
		when(goodsDescriptionDiffer.checkDiff(oldDesc, newDesc)).thenReturn(GoodsDescriptionDiffer.Diff.NO_CHANGE);
		when(goodsDescriptionRepository.existsByGoodsIdAndReviewStatusNot(goods.getId(), GoodsReviewStatus.APPROVED)).thenReturn(false);
		when(goodsRepository.findForUpdateByIdAndReleasedById(goods.getId(), currentUserId)).thenReturn(Optional.of(goods));

		goodsService.update(goods.getId(), newDesc);

		verify(goodsDescriptionRepository, never()).save(any());
		assertSame(oldDesc, goods.getCurrentDescription());
	}

	private void updateOnShelf(boolean existsNotApproved, GoodsDescriptionDiffer.Diff diff) {
		var goods = buildOnShelfGoods();
		var oldDesc = goods.getCurrentDescription();
		assert oldDesc != null;
		var newDesc = buildDescription();
		when(goodsDescriptionDiffer.checkDiff(oldDesc, newDesc)).thenReturn(diff);
		when(goodsDescriptionRepository.existsByGoodsIdAndReviewStatusNot(goods.getId(), GoodsReviewStatus.APPROVED)).thenReturn(existsNotApproved);
		when(goodsRepository.findForUpdateByIdAndReleasedById(goods.getId(), currentUserId)).thenReturn(Optional.of(goods));

		var savedGoods = goodsService.update(goods.getId(), newDesc);

		var inOrder = inOrder(goodsDescriptionRepository, goodsRepository);
		inOrder.verify(goodsDescriptionRepository).deleteByGoodsIdAndReviewStatusNot(goods.getId(), GoodsReviewStatus.APPROVED);
		verifyReplace(inOrder, goods, oldDesc, newDesc);
		assertPublished(savedGoods, newDesc);
	}

	@Test
	public void updatePublishedNoReviewNeeded() {
		updateOnShelf(false, GoodsDescriptionDiffer.Diff.UPDATED);
	}

	@Test
	public void updateOnShelfNotApprovedNoChange() {
		updateOnShelf(true, GoodsDescriptionDiffer.Diff.NO_CHANGE);
	}

	@Test
	public void updateOnShelfNotApprovedNoReviewNeeded() {
		updateOnShelf(true, GoodsDescriptionDiffer.Diff.UPDATED);
	}

	@Test
	public void updateOnShelfReviewNeeded() {
		var goods = buildOnShelfGoods();
		var oldDesc = goods.getCurrentDescription();
		assert oldDesc != null;
		var newDesc = buildDescription();
		when(goodsDescriptionDiffer.checkDiff(oldDesc, newDesc)).thenReturn(GoodsDescriptionDiffer.Diff.NEED_REVIEW);
		when(goodsDescriptionRepository.findByGoodsIdAndReviewStatus(goods.getId(), GoodsReviewStatus.CHANGE_REQUESTED))
			.thenReturn(Optional.of(buildChangeRequestedDescription()));
		when(goodsRepository.findForUpdateByIdAndReleasedById(goods.getId(), currentUserId)).thenReturn(Optional.of(goods));

		var savedGoods = goodsService.update(goods.getId(), newDesc);

		var inOrder = inOrder(goodsDescriptionRepository, goodsRepository);
		inOrder.verify(goodsDescriptionRepository).deleteByGoodsIdAndReviewStatusNot(savedGoods.getId(), GoodsReviewStatus.APPROVED);
		inOrder.verify(goodsDescriptionRepository).save(newDesc);
		assertPendingOnShelf(savedGoods, newDesc);
		assertReviewInfoCopied(newDesc);
	}

	private GoodsDescription buildChangeRequestedDescription() {
		var desc = new GoodsDescription();
		desc.setReviewStatus(GoodsReviewStatus.CHANGE_REQUESTED);
		desc.setReviewComment(reviewComment);
		desc.setReviewedTime(lastReviewTime);
		return desc;
	}

	private GoodsDescription buildOffShelfPendingDescription() {
		var goods = new Goods();
		goods.setId(123L);
		var desc = buildDescription();
		desc.setReviewStatus(GoodsReviewStatus.PENDING);
		desc.setGoods(goods);
		return desc;
	}

	@Test
	public void approveOffShelf() {
		var desc = buildOffShelfPendingDescription();
		var goods = desc.getGoods();

		goodsService.reviewApprove(desc);

		verify(goodsDescriptionRepository).save(desc);
		assertPublished(goods, desc);
		assertEquals(OffsetDateTime.now(clock), desc.getReviewedTime());
	}

	@Test
	public void approveOnShelf() {
		var goods = buildOnShelfGoods();
		var oldDesc = goods.getCurrentDescription();
		assert oldDesc != null;
		var desc = buildDescription();
		desc.setReviewStatus(GoodsReviewStatus.PENDING);
		desc.setGoods(goods);

		goodsService.reviewApprove(desc);

		verifyReplace(goods, oldDesc, desc);
		assertPublished(goods, desc);
		assertEquals(OffsetDateTime.now(clock), desc.getReviewedTime());
	}

	@Test
	public void requestChange() {
		var desc = buildOffShelfPendingDescription();

		goodsService.reviewRequestChange(desc, reviewComment);

		verify(goodsDescriptionRepository).save(desc);
		assertEquals(GoodsReviewStatus.CHANGE_REQUESTED, desc.getReviewStatus());
		assertEquals(reviewComment, desc.getReviewComment());
		assertEquals(OffsetDateTime.now(clock), desc.getReviewedTime());
	}

	@Test
	public void takeOffShelfPublished() {
		var goods = buildOnShelfGoods();
		var desc = goods.getCurrentDescription();
		assert desc != null;
		when(goodsDescriptionRepository.existsByGoodsIdAndReviewStatusNot(goods.getId(), GoodsReviewStatus.APPROVED)).thenReturn(false);

		goodsService.reviewRequestChange(desc, reviewComment);

		verify(goodsRepository).save(goods);
		verify(goodsDescriptionRepository).save(desc);
		assertChangeRequestedOffShelf(goods, desc);
	}

	@Test
	public void takeOffShelfNotApproved() {
		var goods = buildOnShelfGoods();
		var desc = goods.getCurrentDescription();
		assert desc != null;
		when(goodsDescriptionRepository.existsByGoodsIdAndReviewStatusNot(goods.getId(), GoodsReviewStatus.APPROVED)).thenReturn(true);

		goodsService.reviewRequestChange(desc, reviewComment);

		var inOrder = inOrder(goodsDescriptionRepository, goodsRepository);
		inOrder.verify(goodsRepository).save(goods);
		inOrder.verify(goodsDescriptionRepository).delete(desc);
		assertNull(goods.getCurrentDescription());
	}
}
