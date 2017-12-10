package com.rogelioorts.workshop.vertx.microservices.series.data.routing.series;

import com.rogelioorts.workshop.vertx.microservices.series.data.models.Serie;
import com.rogelioorts.workshop.vertx.microservices.series.data.repositories.SeriesRepository;
import com.rogelioorts.workshop.vertx.microservices.shared.utils.Responses;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class ViewSerieHandler implements Handler<RoutingContext> {

  private final SeriesRepository seriesRepository;

  public ViewSerieHandler(final SeriesRepository seriesRepository) {
    this.seriesRepository = seriesRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    final String id = context.pathParam("id");

    seriesRepository.find(id, res -> {
      if (res.failed()) {
        context.fail(res.cause());
      } else {
        final Serie model = res.result();

        if (model == null) {
          context.fail(HttpResponseStatus.NOT_FOUND.code());
        } else {
          Responses.sendJson(context, model);
        }
      }
    });
  }

}
