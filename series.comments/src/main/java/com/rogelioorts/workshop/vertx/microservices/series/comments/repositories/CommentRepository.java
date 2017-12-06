package com.rogelioorts.workshop.vertx.microservices.series.comments.repositories;

import java.time.LocalDateTime;

import com.rogelioorts.workshop.vertx.microservices.series.comments.models.Comment;
import com.rogelioorts.workshop.vertx.microservices.utils.repositories.BaseRepository;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.mongo.MongoClient;

public class CommentRepository extends BaseRepository<Comment> {

  public static final int DEFAULT_PER_PAGE = 20;

  private static final String COLLECTION = "comments";

  public CommentRepository(final MongoClient client) {
    super(client, Comment.class);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION;
  }

  @Override
  protected int getDefaultResultsPerPage() {
    return DEFAULT_PER_PAGE;
  }

  @Override
  protected void beforeInsert(final Comment model, final Handler<AsyncResult<Void>> handler) {
    model.setCreationDate(LocalDateTime.now());
    model.setUpdateDate(LocalDateTime.now());

    handler.handle(Future.succeededFuture());
  }

  @Override
  protected void beforeUpdate(final Comment model, final Handler<AsyncResult<Void>> handler) {
    model.setUpdateDate(LocalDateTime.now());

    handler.handle(Future.succeededFuture());
  }

}
