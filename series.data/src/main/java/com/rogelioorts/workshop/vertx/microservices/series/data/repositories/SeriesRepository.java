package com.rogelioorts.workshop.vertx.microservices.series.data.repositories;

import java.time.LocalDateTime;

import com.rogelioorts.workshop.vertx.microservices.scafolder.repositories.BaseRepository;
import com.rogelioorts.workshop.vertx.microservices.series.data.models.Serie;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.mongo.MongoClient;

public class SeriesRepository extends BaseRepository<Serie> {

  public static final int DEFAULT_PER_PAGE = 20;

  private static final String COLLECTION = "series";

  public SeriesRepository(final MongoClient client) {
    super(client, Serie.class);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION;
  }

  @Override
  protected void beforeInsert(final Serie model, final Handler<AsyncResult<Void>> handler) {
    model.setCreationDate(LocalDateTime.now());
    model.setUpdateDate(LocalDateTime.now());

    handler.handle(Future.succeededFuture());
  }

  @Override
  protected void beforeUpdate(final Serie model, final Handler<AsyncResult<Void>> handler) {
    model.setUpdateDate(LocalDateTime.now());

    handler.handle(Future.succeededFuture());
  }

}
