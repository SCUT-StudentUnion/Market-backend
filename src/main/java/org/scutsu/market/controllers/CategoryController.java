package org.scutsu.market.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import org.scutsu.market.models.Category;
import org.scutsu.market.models.Goods;
import org.scutsu.market.models.Views;
import org.scutsu.market.repositories.CategoryRepository;
import org.scutsu.market.services.GoodsReadService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categories")
@AllArgsConstructor
public class CategoryController {

	private final CategoryRepository categoryRepository;
	private final GoodsReadService goodsReadService;

	@GetMapping
	public Iterable<Category> getAll() {
		return categoryRepository.findAll();
	}

	@GetMapping("/{id}/goods")
	@JsonView(Views.Goods.List.class)
	public PagedEntity<Goods> getGoods(@PathVariable long id, Pageable pageable) {
		return new PagedEntity<>(goodsReadService.getAllByCategoryId(id, pageable));
	}
}
