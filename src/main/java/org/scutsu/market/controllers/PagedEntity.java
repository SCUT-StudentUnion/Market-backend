package org.scutsu.market.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.scutsu.market.models.Views;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@JsonView(Views.Minimum.class)
class PagedEntity<T> {

	private final int totalPages;
	private final List<T> content;

	PagedEntity(Page<T> result) {
		this.totalPages = result.getTotalPages();
		this.content = result.getContent();
	}
}
