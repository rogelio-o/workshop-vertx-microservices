package com.rogelioorts.workshop.vertx.microservices.series.comments.routing.comments;

import java.util.NoSuchElementException;

import com.rogelioorts.workshop.vertx.microservices.scafolder.utils.Responses;
import com.rogelioorts.workshop.vertx.microservices.series.comments.repositories.CommentsRepository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class DeleteCommentHandler implements Handler<RoutingContext> {

  private final CommentsRepository commentsRepository;

  public DeleteCommentHandler(final CommentsRepository commentsRepository) {
    this.commentsRepository = commentsRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    final String id = context.pathParam("id");

    commentsRepository.delete(id, res -> {
      if (res.failed()) {
        final Throwable exception = res.cause();

        if (exception.getClass().equals(NoSuchElementException.class)) {
          context.fail(res.cause());
        } else {
          sendOkResponse(context);
        }
      } else {
        sendOkResponse(context);
      }
    });
  }

  private void sendOkResponse(final RoutingContext context) {
    Responses.sendJson(context, new JsonObject(), HttpResponseStatus.NO_CONTENT.code());
  }

}
