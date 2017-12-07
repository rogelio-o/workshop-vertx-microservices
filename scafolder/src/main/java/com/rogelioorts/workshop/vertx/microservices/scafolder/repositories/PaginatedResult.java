package com.rogelioorts.workshop.vertx.microservices.scafolder.repositories;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedResult<T> {

  private List<T> results;

  private int page;

  private int perPage;

  private long totalResults;

  public long getTotalPages() {
    return (long) Math.ceil((double) totalResults / perPage);
  }

}
