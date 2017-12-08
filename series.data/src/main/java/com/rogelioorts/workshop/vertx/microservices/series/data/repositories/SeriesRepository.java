package com.rogelioorts.workshop.vertx.microservices.series.data.repositories;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

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

  public void findAndUpdateRatingsStatisticsIfNotUpdated(final String id, final LocalDateTime msgReceivedDate, final double averageRating,
      final long numRatings, final Handler<AsyncResult<Serie>> handler) {
    findAndUpdateIfNotUpdated(id, msgReceivedDate, model -> {
      model.setAverageRating(averageRating);
      model.setNumRatings(numRatings);
    }, handler);
  }

  public void findAndUpdateCommentsStatisticsIfNotUpdatedIfNotUpdated(final String id, final LocalDateTime msgReceivedDate, final long numComments,
      final Handler<AsyncResult<Serie>> handler) {
    findAndUpdateIfNotUpdated(id, msgReceivedDate, model -> model.setNumComments(numComments), handler);
  }

  private void findAndUpdateIfNotUpdated(final String id, final LocalDateTime msgReceivedDate, final Consumer<Serie> transformer,
      final Handler<AsyncResult<Serie>> handler) {
    find(id, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        final Serie model = res.result();

        if (model == null) {
          handler.handle(Future.failedFuture(new NoSuchElementException("Not found document with ID " + id)));
        } else if (model.getUpdateDate().isBefore(msgReceivedDate)) {
          transformer.accept(model);

          update(id, model, handler);
        } else {
          handler.handle(Future.failedFuture(new IllegalStateException("It has already been updated.")));
        }
      }
    });
  }

}
