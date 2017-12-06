package com.rogelioorts.workshop.vertx.microservices.series.comments.controllers;

import java.time.LocalDateTime;

import com.rogelioorts.workshop.vertx.microservices.series.comments.models.Comment;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class CreateAction implements Handler<RoutingContext> {

  private final Logger log = LoggerFactory.getLogger(CreateAction.class);

  @Override
  public void handle(final RoutingContext context) {
    log.debug("Create comment request received.");

    Comment comment = new Comment();
    comment.setText("prueba");
    comment.setCreationDate(LocalDateTime.now());
    context.response().end(JsonObject.mapFrom(comment).toBuffer());
  }

}
