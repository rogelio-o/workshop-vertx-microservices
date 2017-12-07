package com.rogelioorts.workshop.vertx.microservices.edge.exceptions;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class ExceptionHandler implements Handler<RoutingContext> {

  private final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

  @Override
  public void handle(final RoutingContext context) {
    final Throwable exception = context.failure();

    log.error("Error in controller action.", exception);

    final JsonObject response = new JsonObject().put("error", true).put("message", exception.getMessage());
    context.response().end(response.encode());
  }

}
