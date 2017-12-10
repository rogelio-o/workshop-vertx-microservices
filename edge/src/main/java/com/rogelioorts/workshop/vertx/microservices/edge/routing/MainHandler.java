package com.rogelioorts.workshop.vertx.microservices.edge.routing;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.PebbleTemplateEngine;

public class MainHandler implements Handler<RoutingContext> {

  private static final String TEMPLATE_PATH = "templates/";
  private static final String INDEX_TEMPLATE = "index";

  private final PebbleTemplateEngine templateEngine;

  public MainHandler(final PebbleTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  @Override
  public void handle(final RoutingContext context) {
    // #PLACEHOLDER-25c
  }

}
