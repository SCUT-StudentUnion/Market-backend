package org.scutsu.market.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import org.scutsu.market.models.Category;
import org.scutsu.market.models.Goods;
import org.scutsu.market.models.Views;
import org.scutsu.market.repositories.CategoryRepository;
import org.scutsu.market.repositories.GoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categories")
public class CategoryController {

	private final CategoryRepository categoryRepository;
	private final GoodsRepository goodsRepository;

	@Autowired
	public CategoryController(CategoryRepository categoryRepository, GoodsRepository goodsRepository) {
		this.categoryRepository = categoryRepository;
		this.goodsRepository = goodsRepository;
	}

	@GetMapping
	public Iterable<Category> getAll() {
		return categoryRepository.findAll();
	}

	@GetMapping("/{id}/goods")
	@JsonView(Views.Goods.List.class)
	public PagedEntity<Goods> getAllByCategory(@PathVariable long id, Pageable pageable) {
		return new PagedEntity<>(goodsRepository.findAllByCurrentDescriptionCategoryId(id, pageable));
	}
}
