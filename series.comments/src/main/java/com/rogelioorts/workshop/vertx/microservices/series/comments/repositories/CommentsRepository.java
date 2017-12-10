package com.rogelioorts.workshop.vertx.microservices.series.comments.repositories;

import java.time.LocalDateTime;

import com.rogelioorts.workshop.vertx.microservices.series.comments.models.Comment;
import com.rogelioorts.workshop.vertx.microservices.series.comments.models.NewCommentMessage;
import com.rogelioorts.workshop.vertx.microservices.series.comments.models.RemoveCommentMessage;
import com.rogelioorts.workshop.vertx.microservices.shared.repositories.BaseRepository;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class CommentsRepository extends BaseRepository<Comment> {

  public static final int DEFAULT_PER_PAGE = 20;

  private static final String COLLECTION = "comments";

  private static final String NEW_COMMENT_BUS_MSG = "new.comment";

  private static final String REMOVE_COMMENT_BUS_MSG = "remove.comment";

  private final Vertx vertx;

  private final MongoClient client;

  public CommentsRepository(final Vertx vertx, final MongoClient client) {
    super(client, Comment.class);

    this.vertx = vertx;
    this.client = client;
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION;
  }

  @Override
  protected void beforeInsert(final Comment model, final Handler<AsyncResult<Void>> handler) {
    model.setCreationDate(LocalDateTime.now());
    model.setUpdateDate(LocalDateTime.now());

    handler.handle(Future.succeededFuture());
  }

  @Override
  protected void afterInsert(final Comment model, final Handler<AsyncResult<Void>> handler) {
    sendNewCommentBusMessage(model, handler);
  }

  private void sendNewCommentBusMessage(final Comment model, final Handler<AsyncResult<Void>> handler) {
    getTotal(model.getIdSerie(), res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        final EventBus eventBus = vertx.eventBus();
        final long total = res.result();
        final NewCommentMessage newMessage = new NewCommentMessage();
        newMessage.setIdSerie(model.getIdSerie());
        newMessage.setComment(model);
        newMessage.setTotal(total);
        eventBus.publish(NEW_COMMENT_BUS_MSG, JsonObject.mapFrom(newMessage));

        handler.handle(Future.succeededFuture());
      }
    });
  }

  @Override
  protected void afterDelete(final Comment model, final Handler<AsyncResult<Void>> handler) {
    sendRemoveCommentBusMessage(model, handler);
  }

  private void sendRemoveCommentBusMessage(final Comment model, final Handler<AsyncResult<Void>> handler) {
    getTotal(model.getIdSerie(), res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        final EventBus eventBus = vertx.eventBus();
        final RemoveCommentMessage removeMessage = new RemoveCommentMessage();
        removeMessage.setId(model.getId());
        removeMessage.setIdSerie(model.getIdSerie());
        removeMessage.setTotal(res.result());
        eventBus.publish(REMOVE_COMMENT_BUS_MSG, JsonObject.mapFrom(removeMessage));

        handler.handle(Future.succeededFuture());
      }
    });
  }

  @Override
  protected void beforeUpdate(final Comment model, final Handler<AsyncResult<Void>> handler) {
    model.setUpdateDate(LocalDateTime.now());

    handler.handle(Future.succeededFuture());
  }

  private void getTotal(final String idSerie, final Handler<AsyncResult<Long>> handler) {
    final JsonObject query = new JsonObject().put("id_serie", idSerie);
    client.count(COLLECTION, query, handler);
  }

}
