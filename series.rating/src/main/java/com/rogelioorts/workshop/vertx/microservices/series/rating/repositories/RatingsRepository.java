package com.rogelioorts.workshop.vertx.microservices.series.rating.repositories;

import java.time.LocalDateTime;

import com.rogelioorts.workshop.vertx.microservices.scafolder.repositories.BaseRepository;
import com.rogelioorts.workshop.vertx.microservices.series.rating.models.Rating;
import com.rogelioorts.workshop.vertx.microservices.series.rating.models.RatingStatistics;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class RatingsRepository extends BaseRepository<Rating> {

  public static final int DEFAULT_PER_PAGE = 20;

  private static final String COLLECTION = "ratings";

  private static final String NEW_RATING_BUS_MSG = "new.rating";

  private final Vertx vertx;

  private final MongoClient client;

  public RatingsRepository(final Vertx vertx, final MongoClient client) {
    super(client, Rating.class);

    this.vertx = vertx;
    this.client = client;
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION;
  }

  @Override
  protected void beforeInsert(final Rating model, final Handler<AsyncResult<Void>> handler) {
    model.setCreationDate(LocalDateTime.now());

    handler.handle(Future.succeededFuture());
  }

  @Override
  protected void afterInsert(final Rating model, final Handler<AsyncResult<Void>> handler) {
    sendNewRatingBusMessage(model, handler);
  }

  private void sendNewRatingBusMessage(final Rating model, final Handler<AsyncResult<Void>> handler) {
    getAverageAndTotal(model.getIdSerie(), res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        final EventBus eventBus = vertx.eventBus();
        final RatingStatistics statistics = res.result();
        eventBus.publish(NEW_RATING_BUS_MSG, JsonObject.mapFrom(statistics));

        handler.handle(Future.succeededFuture());
      }
    });
  }

  private void getAverageAndTotal(final String idSerie, final Handler<AsyncResult<RatingStatistics>> handler) {
    final Future<Double> fAverage = Future.future();
    getAverage(idSerie, fAverage.completer());

    final Future<Long> fTotal = Future.future();
    getTotal(idSerie, fTotal.completer());

    CompositeFuture.all(fAverage, fTotal).setHandler(res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(new RatingStatistics(idSerie, fAverage.result(), fTotal.result())));
      }
    });
  }

  private void getTotal(final String idSerie, final Handler<AsyncResult<Long>> handler) {
    final JsonObject query = new JsonObject().put("id_serie", idSerie);
    client.count(COLLECTION, query, handler);
  }

  private void getAverage(final String idSerie, final Handler<AsyncResult<Double>> handler) {
    final JsonObject matchPipeline = new JsonObject().put("$match", new JsonObject().put("id_serie", idSerie));
    final JsonObject avgPipeline = new JsonObject().put("$group",
        new JsonObject().put("_id", "$id_serie").put("avg", new JsonObject().put("$avg", "$puntuation")));
    final JsonObject cursorOptions = new JsonObject().put("batchSize", 1);
    final JsonArray pipeline = new JsonArray().add(matchPipeline).add(avgPipeline);
    final JsonObject command = new JsonObject().put("aggregate", COLLECTION).put("cursor", cursorOptions).put("pipeline", pipeline);

    client.runCommand("aggregate", command, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        final JsonObject cursor = res.result().getJsonObject("cursor");
        final JsonArray firstBatch = cursor.getJsonArray("firstBatch");
        final JsonObject document = firstBatch.isEmpty() ? new JsonObject() : firstBatch.getJsonObject(0);

        handler.handle(Future.succeededFuture(document.getDouble("avg", 0d)));
      }
    });
  }

}
