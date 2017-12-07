package com.rogelioorts.workshop.vertx.microservices.series.data.routing.series;

import com.rogelioorts.workshop.vertx.microservices.scafolder.utils.Requests;
import com.rogelioorts.workshop.vertx.microservices.scafolder.utils.Responses;
import com.rogelioorts.workshop.vertx.microservices.series.data.models.Serie;
import com.rogelioorts.workshop.vertx.microservices.series.data.repositories.SeriesRepository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class CreateSerieHandler implements Handler<RoutingContext> {

  private final SeriesRepository seriesRepository;

  public CreateSerieHandler(final SeriesRepository seriesRepository) {
    this.seriesRepository = seriesRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    Requests.bodyAsObjectAndValidate(context, Serie.class, model -> {
      seriesRepository.insert(model, res -> {
        if (res.failed()) {
          context.fail(res.cause());
        } else {
          Responses.sendJson(context, model, HttpResponseStatus.CREATED.code());
        }
      });
    });
  }

}
