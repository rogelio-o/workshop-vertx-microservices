package com.rogelioorts.workshop.vertx.microservices.series.comments.repositories;

import java.time.LocalDateTime;

import com.rogelioorts.workshop.vertx.microservices.scafolder.repositories.BaseRepository;
import com.rogelioorts.workshop.vertx.microservices.series.comments.models.Comment;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class CommentsRepository extends BaseRepository<Comment> {

  public static final int DEFAULT_PER_PAGE = 20;

  private static final String COLLECTION = "comments";

  private final MongoClient client;

  public CommentsRepository(final MongoClient client) {
    super(client, Comment.class);

    this.client = client;
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
  protected JsonObject getPaginationSort() {
    return new JsonObject().put("creation_date", -1);
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

  public void getTotal(final String idSerie, final Handler<AsyncResult<Long>> handler) {
    final JsonObject query = new JsonObject().put("id_serie", idSerie);
    client.count(COLLECTION, query, handler);
  }

}
