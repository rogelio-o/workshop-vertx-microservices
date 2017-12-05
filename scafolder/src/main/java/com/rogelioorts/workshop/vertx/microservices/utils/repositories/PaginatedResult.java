package com.rogelioorts.workshop.vertx.microservices.utils.repositories;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResult<T> {

	private List<T> result;
	
	private int perPage;
	
	private long totalResults;
	
	public long getTotalPages() {
		return (long) Math.ceil((double) totalResults / perPage);
	}
	
}
