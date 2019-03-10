package org.scutsu.market.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.scutsu.market.models.Favorite;
import org.scutsu.market.models.Goods;
import org.scutsu.market.models.GoodsDescription;
import org.scutsu.market.models.Views;
import org.scutsu.market.services.FavoriteService;
import org.scutsu.market.services.GoodsReadService;
import org.scutsu.market.services.GoodsService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/goods")
@AllArgsConstructor
public class GoodsController {

	private final GoodsService goodsService;
	private final GoodsReadService goodsReadService;
	private final FavoriteService favoriteService;

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	@JsonView(Views.Minimum.class)
	public Goods create(@RequestBody @JsonView(Views.Goods.UserAccessible.class) GoodsDescription desc) {
		return goodsService.create(desc);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	@JsonView(Views.Minimum.class)
	public Goods update(@PathVariable("id") long goodsId,
						@RequestBody @JsonView(Views.Goods.UserAccessible.class) GoodsDescription desc) {
		return goodsService.update(goodsId, desc);
	}

	@GetMapping
	@JsonView(Views.Goods.List.class)
	public PagedEntity<Goods> getAll(Pageable pageable) {
		return new PagedEntity<>(goodsReadService.getAll(pageable));
	}

	@GetMapping("/{id}")
	@JsonView(Views.Goods.Detail.class)
	public Goods get(@PathVariable("id") long goodsId) {
		return goodsReadService.get(goodsId);
	}

	@GetMapping("/favorite")
	@PreAuthorize("hasRole('USER')")
	@JsonView(Views.Goods.List.class)
	public PagedEntity<Favorite> getCollected(Pageable pageable) {
		return new PagedEntity<>(goodsReadService.getFavorite(pageable));
	}

	@GetMapping("/my")
	@PreAuthorize("hasRole('USER')")
	@JsonView(Views.Goods.Self.class)
	public PagedEntity<GoodsDescription> getAllMy(Pageable pageable) {
		return new PagedEntity<>(goodsReadService.getMy(pageable));
	}

	@GetMapping("/my/{id}")
	@PreAuthorize("hasRole('USER')")
	@JsonView(Views.Goods.Self.class)
	public GoodsDescription getMy(@PathVariable("id") long descId) {
		return goodsReadService.getMy(descId);
	}

	@PostMapping("/{id}/addToFavorite")
	@PreAuthorize("hasRole('USER')")
	public SuccessResult addToFavorite(@PathVariable("id") long goodsId) {
		favoriteService.add(goodsId);
		return new SuccessResult();
	}

	@PostMapping("/{id}/deleteFromFavorite")
	@PreAuthorize("hasRole('USER')")
	public SuccessResult deleteFromFavorite(@PathVariable("id") long goodsId) {
		favoriteService.delete(goodsId);
		return new SuccessResult();
	}

	@GetMapping("/needReview")
	@JsonView(Views.Goods.Admin.class)
	@PreAuthorize("hasRole('ADMIN')")
	public PagedEntity<GoodsDescription> getAllNeedReview(Pageable pageable) {
		return new PagedEntity<>(goodsReadService.getAllNeedReview(pageable));
	}

	@GetMapping("/changeRequested")
	@JsonView(Views.Goods.Admin.class)
	@PreAuthorize("hasRole('ADMIN')")
	public PagedEntity<GoodsDescription> getAllChangeRequested(Pageable pageable) {
		return new PagedEntity<>(goodsReadService.getAllChangeRequested(pageable));
	}

	@PostMapping("/{id}/review/approve")
	@PreAuthorize("hasRole('ADMIN')")
	@JsonView(Views.Minimum.class)
	public SuccessResult reviewApprove(@PathVariable("id") long descriptionId) {
		goodsService.reviewApprove(descriptionId);
		return new SuccessResult();
	}

	@PostMapping("/{id}/review/requestChange")
	@PreAuthorize("hasRole('ADMIN')")
	@JsonView(Views.Minimum.class)
	public SuccessResult reviewRequestChange(@PathVariable("id") long descriptionId,
											 @RequestBody @Valid RequestChangeForm comment) {
		goodsService.reviewRequestChange(descriptionId, comment.getComment());
		return new SuccessResult();
	}

	@Data
	private static class RequestChangeForm {
		@NotNull
		private String comment;
	}
}
