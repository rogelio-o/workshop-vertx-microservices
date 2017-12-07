package com.rogelioorts.workshop.vertx.microservices.series.comments.routing.comments;

import com.rogelioorts.workshop.vertx.microservices.scafolder.utils.Requests;
import com.rogelioorts.workshop.vertx.microservices.scafolder.utils.Responses;
import com.rogelioorts.workshop.vertx.microservices.series.comments.models.Comment;
import com.rogelioorts.workshop.vertx.microservices.series.comments.repositories.CommentsRepository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class CreateCommentHandler implements Handler<RoutingContext> {

  private final CommentsRepository commentsRepository;

  public CreateCommentHandler(final CommentsRepository commentsRepository) {
    this.commentsRepository = commentsRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    Requests.bodyAsObjectAndValidate(context, Comment.class, model -> {
      commentsRepository.insert(model, res -> {
        if (res.failed()) {
          context.fail(res.cause());
        } else {
          Responses.sendJson(context, model, HttpResponseStatus.CREATED.code());
        }
      });
    });
  }

}
