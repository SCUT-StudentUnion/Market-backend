package org.scutsu.market.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scutsu.market.models.Goods;
import org.scutsu.market.models.Views;
import org.scutsu.market.services.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/goods")
public class GoodsController {

	private final GoodsService goodsService;
	private final ObjectMapper objectMapper;

	@Autowired
	public GoodsController(GoodsService goodsService, ObjectMapper objectMapper) {
		this.goodsService = goodsService;
		this.objectMapper = objectMapper;
	}

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	@JsonView(Views.Minimum.class)
	public Goods create(@RequestBody @JsonView(Views.Goods.UserAccessible.class) Goods goods) {
		return goodsService.create(goods);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('USER') && #goods.releasedBy.id == principal.userId || hasRole('ADMIN')")
	@JsonView(Views.Minimum.class)
	public Goods update(@PathVariable("id") Goods goods, @RequestBody JsonNode json) throws IOException {
		objectMapper.readerForUpdating(goods).withView(Views.Goods.UserAccessible.class).readValue(json);
		return goodsService.update(goods);
	}

	@GetMapping
	@JsonView(Views.Goods.List.class)
	public Iterable<Goods> getAll() {
		return goodsService.getAll();
	}

	@PostMapping("/{id}/auditPass")
	@PreAuthorize("hasRole('ADMIN')")
	public void auditPass(@PathVariable("id") Goods goods) {
		goodsService.auditPass(goods);
	}
}
