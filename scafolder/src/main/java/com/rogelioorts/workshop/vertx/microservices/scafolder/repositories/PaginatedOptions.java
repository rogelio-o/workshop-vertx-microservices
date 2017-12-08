package com.rogelioorts.workshop.vertx.microservices.scafolder.repositories;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedOptions {

  private static final String PAGE_PATH_NAME = "page";
  private static final String PER_PAGE_PATH_NAME = "per_page";

  private int page; // First is 1

  private Integer perPage;

  private JsonObject query;

  private JsonObject sort;

  @JsonIgnore
  public int getSkip() {
    return (page - 1) * perPage;
  }

  public static PaginatedOptions create(final RoutingContext context, final int defaultPerPage, final JsonObject query, final JsonObject sort) {
    final MultiMap queryParams = context.queryParams();

    final PaginatedOptions result = new PaginatedOptions();
    result.setSort(sort);
    result.setQuery(query);
    result.setPage(queryParams.get(PAGE_PATH_NAME) == null ? 1 : Integer.valueOf(queryParams.get(PAGE_PATH_NAME)));
    result.setPerPage(queryParams.get(PER_PAGE_PATH_NAME) == null ? defaultPerPage : Integer.valueOf(queryParams.get(PER_PAGE_PATH_NAME)));

    return result;
  }

}
