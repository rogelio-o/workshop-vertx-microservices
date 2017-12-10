package com.rogelioorts.workshop.vertx.microservices.series.data.routing.series;

import com.rogelioorts.workshop.vertx.microservices.series.data.repositories.SeriesRepository;
import com.rogelioorts.workshop.vertx.microservices.shared.repositories.PaginatedOptions;
import com.rogelioorts.workshop.vertx.microservices.shared.utils.Responses;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ListSeriesHandler implements Handler<RoutingContext> {

  private final SeriesRepository seriesRepository;

  public ListSeriesHandler(final SeriesRepository seriesRepository) {
    this.seriesRepository = seriesRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    final JsonObject query = new JsonObject();
    final JsonObject sort = new JsonObject().put("name", 1);
    final PaginatedOptions paginatedOptions = PaginatedOptions.create(context, SeriesRepository.DEFAULT_PER_PAGE, query, sort);

    seriesRepository.findPaginated(paginatedOptions, res -> {
      if (res.failed()) {
        context.fail(res.cause());
      } else {
        Responses.sendJson(context, res.result());
      }
    });
  }

}
