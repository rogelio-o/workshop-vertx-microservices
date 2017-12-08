package com.rogelioorts.workshop.vertx.microservices.scafolder.exceptions;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class ResourceNotFoundHandler implements Handler<RoutingContext> {

  @Override
  public void handle(final RoutingContext context) {
    throw new ResourceNotFoundException(context.normalisedPath());
  }

}
