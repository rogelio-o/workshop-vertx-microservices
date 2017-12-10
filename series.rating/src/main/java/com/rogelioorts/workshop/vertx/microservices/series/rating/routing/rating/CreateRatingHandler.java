package com.rogelioorts.workshop.vertx.microservices.series.rating.routing.rating;

import com.rogelioorts.workshop.vertx.microservices.series.rating.models.Rating;
import com.rogelioorts.workshop.vertx.microservices.series.rating.repositories.RatingsRepository;
import com.rogelioorts.workshop.vertx.microservices.shared.utils.Requests;
import com.rogelioorts.workshop.vertx.microservices.shared.utils.Responses;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class CreateRatingHandler implements Handler<RoutingContext> {

  private final RatingsRepository ratingsRepository;

  public CreateRatingHandler(final RatingsRepository ratingsRepository) {
    this.ratingsRepository = ratingsRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    final String idSerie = context.pathParam("idSerie");

    Requests.bodyAsObjectAndValidate(context, Rating.class, model -> model.setIdSerie(idSerie), model -> {
      ratingsRepository.insert(model, res -> {
        if (res.failed()) {
          context.fail(res.cause());
        } else {
          Responses.sendJson(context, model, HttpResponseStatus.CREATED.code());
        }
      });
    });
  }

}
