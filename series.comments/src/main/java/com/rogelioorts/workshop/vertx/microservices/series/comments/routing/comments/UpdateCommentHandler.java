package com.rogelioorts.workshop.vertx.microservices.series.comments.routing.comments;

import java.util.NoSuchElementException;

import com.rogelioorts.workshop.vertx.microservices.scafolder.utils.Requests;
import com.rogelioorts.workshop.vertx.microservices.scafolder.utils.Responses;
import com.rogelioorts.workshop.vertx.microservices.series.comments.models.Comment;
import com.rogelioorts.workshop.vertx.microservices.series.comments.repositories.CommentsRepository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class UpdateCommentHandler implements Handler<RoutingContext> {

  private final CommentsRepository commentsRepositor;

  public UpdateCommentHandler(final CommentsRepository commentsRepositor) {
    this.commentsRepositor = commentsRepositor;
  }

  @Override
  public void handle(final RoutingContext context) {
    final String id = context.pathParam("id");

    Requests.bodyAsObjectAndValidate(context, Comment.class, model -> {
      commentsRepositor.update(id, model, res -> {
        if (res.failed()) {
          if (res.cause().getClass().equals(NoSuchElementException.class)) {
            context.fail(HttpResponseStatus.NOT_FOUND.code());
          } else {
            context.fail(res.cause());
          }
        } else {
          Responses.sendJson(context, res.result());
        }
      });
    });
  }

}
