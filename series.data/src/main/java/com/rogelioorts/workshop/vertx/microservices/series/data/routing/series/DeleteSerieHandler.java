package com.rogelioorts.workshop.vertx.microservices.series.data.routing.series;

import java.util.NoSuchElementException;

import com.rogelioorts.workshop.vertx.microservices.series.data.repositories.SeriesRepository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class DeleteSerieHandler implements Handler<RoutingContext> {

  private final SeriesRepository seriesRepository;

  public DeleteSerieHandler(final SeriesRepository seriesRepository) {
    this.seriesRepository = seriesRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    final String id = context.pathParam("id");

    seriesRepository.delete(id, res -> {
      if (res.failed()) {
        final Throwable exception = res.cause();

        if (exception.getClass().equals(NoSuchElementException.class)) {
          sendOkResponse(context);
        } else {
          context.fail(res.cause());
        }
      } else {
        sendOkResponse(context);
      }
    });
  }

  private void sendOkResponse(final RoutingContext context) {
    context.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end();
  }

}
