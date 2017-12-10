package com.rogelioorts.workshop.vertx.microservices.series.episodes.repositories;

import java.time.LocalDateTime;

import com.rogelioorts.workshop.vertx.microservices.series.episodes.models.Episode;
import com.rogelioorts.workshop.vertx.microservices.shared.repositories.BaseRepository;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.mongo.MongoClient;

public class EpisodesRepository extends BaseRepository<Episode> {

  public static final int DEFAULT_PER_PAGE = 20;

  private static final String COLLECTION = "episodes";

  public EpisodesRepository(final MongoClient client) {
    super(client, Episode.class);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION;
  }

  @Override
  protected void beforeInsert(final Episode model, final Handler<AsyncResult<Void>> handler) {
    model.setCreationDate(LocalDateTime.now());
    model.setUpdateDate(LocalDateTime.now());

    handler.handle(Future.succeededFuture());
  }

  @Override
  protected void beforeUpdate(final Episode model, final Handler<AsyncResult<Void>> handler) {
    model.setUpdateDate(LocalDateTime.now());

    handler.handle(Future.succeededFuture());
  }

}
