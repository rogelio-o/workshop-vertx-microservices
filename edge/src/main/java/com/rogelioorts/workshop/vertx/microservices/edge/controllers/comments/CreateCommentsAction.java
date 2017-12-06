package com.rogelioorts.workshop.vertx.microservices.edge.controllers.comments;

import com.rogelioorts.workshop.vertx.microservices.utils.services.DiscoveryService;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public class CreateCommentsAction implements Handler<RoutingContext> {

  public static final String PATH = "/api/v1/comments";

  @Override
  public void handle(final RoutingContext context) {
    DiscoveryService.callService("series.comments", HttpMethod.POST, PATH, res -> {
      if (res.failed()) {
        context.fail(res.cause());
      } else {
        context.response().end(res.result());
      }
    });
  }

}
