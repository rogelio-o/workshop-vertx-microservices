package com.rogelioorts.workshop.vertx.microservices.series.rating.controllers;

import java.util.Set;

import javax.validation.ConstraintViolation;

import com.rogelioorts.workshop.vertx.microservices.scafolder.repositories.ModelsValidator;
import com.rogelioorts.workshop.vertx.microservices.series.rating.models.Rating;
import com.rogelioorts.workshop.vertx.microservices.series.rating.repositories.RatingsRepository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class CreateController implements Handler<RoutingContext> {

  private final RatingsRepository ratingsRepository;

  public CreateController(final RatingsRepository ratingsRepository) {
    this.ratingsRepository = ratingsRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    final JsonObject body = context.getBodyAsJson();
    if (body == null) {
      context.fail(HttpResponseStatus.BAD_REQUEST.code());
    } else {
      final Rating model = body.mapTo(Rating.class);

      final Set<ConstraintViolation<Rating>> validationErrors = ModelsValidator.validate(model);
      if (validationErrors.isEmpty()) {
        ratingsRepository.insert(model, res -> {
          if (res.failed()) {
            context.fail(res.cause());
          } else {
            context.response().setStatusCode(HttpResponseStatus.CREATED.code()).end(JsonObject.mapFrom(model).toBuffer());
          }
        });
      } else {
        context.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end(ModelsValidator.getErrorBody(validationErrors).toBuffer());
      }
    }
  }

}
