package org.scutsu.market.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.scutsu.market.models.Goods;
import org.scutsu.market.models.GoodsDescription;
import org.scutsu.market.models.Views;
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

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	@JsonView(Views.Minimum.class)
	public Goods create(@RequestBody @JsonView(Views.Goods.UserAccessible.class) GoodsDescription desc) {
		return goodsService.create(desc);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('USER') && #goods.releasedBy.id == principal.userId || hasRole('ADMIN')")
	@JsonView(Views.Minimum.class)
	public Goods update(@PathVariable("id") Goods goods,
						@RequestBody @JsonView(Views.Goods.UserAccessible.class) GoodsDescription desc) {
		return goodsService.update(goods, desc);
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
	public GoodsDescription reviewApprove(@PathVariable("id") GoodsDescription desc) {
		goodsService.reviewApprove(desc);
		return desc;
	}

	@PostMapping("/{id}/review/requestChange")
	@PreAuthorize("hasRole('ADMIN')")
	@JsonView(Views.Minimum.class)
	public GoodsDescription reviewRequestChange(@PathVariable("id") GoodsDescription desc,
									@RequestBody @Valid RequestChangeForm comments) {
		goodsService.reviewRequestChange(desc, comments.getComments());
		return desc;
	}

	@Data
	private static class RequestChangeForm {
		@NotNull
		private String comments;
	}
}
