package com.rogelioorts.workshop.vertx.microservices.series.data.events.rating;

import java.time.LocalDateTime;
import java.util.UUID;

import com.rogelioorts.workshop.vertx.microservices.series.data.repositories.SeriesRepository;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.Lock;
import io.vertx.core.shareddata.SharedData;

public class NewRatingHandler implements Handler<Message<JsonObject>> {

  public static final String ADDRESS = "new.rating";

  private static final String LOCK_PREFIX = "new.rating.";

  private final Logger log = LoggerFactory.getLogger(NewRatingHandler.class);

  private final Vertx vertx;

  private final SeriesRepository seriesRepository;

  public NewRatingHandler(final Vertx vertx, final SeriesRepository seriesRepository) {
    this.vertx = vertx;
    this.seriesRepository = seriesRepository;
  }

  @Override
  public void handle(final Message<JsonObject> message) {
    final JsonObject body = message.body();
    final LocalDateTime msgReceivedDate = LocalDateTime.now();
    final String messageID = UUID.randomUUID().toString();

    log.info("BUS MESSAGE [ID: {0}, ADDRESS: {1}] Received at {2}: {3}.", messageID, ADDRESS, msgReceivedDate, body);

    final String idSerie = body == null ? null : body.getString("id_serie", null);

    if (idSerie == null) {
      log.warn("BUS MESSAGE [ID: {0}, ADDRESS: {1}] There is no id_serie into the message.", messageID, ADDRESS);
    } else {
      final SharedData sharedDate = vertx.sharedData();
      sharedDate.getLock(LOCK_PREFIX + idSerie, lockRes -> {
        if (lockRes.failed()) {
          log.error("BUS MESSAGE [ID: {0}, ADDRESS: {1}] Error getting lock.", messageID, ADDRESS, lockRes.cause());
        } else {
          final Lock lock = lockRes.result();

          try {
            final double average = body.getDouble("average", 0d);
            final long total = body.getLong("total", 0L);

            seriesRepository.findAndUpdateRatingsStatisticsIfNotUpdated(idSerie, msgReceivedDate, average, total, res -> {
              lock.release();

              if (res.failed()) {
                final Throwable exception = res.cause();

                if (IllegalArgumentException.class.equals(exception.getClass())) {
                  log.info("BUS MESSAGE [ID: {0}, ADDRESS: {1}] Previously updated.", messageID, ADDRESS);
                } else {
                  log.error("BUS MESSAGE [ID: {0}, ADDRESS: {1}] Error updating document.", messageID, ADDRESS, res.cause());
                }
              } else {
                log.info("BUS MESSAGE [ID: {0}, ADDRESS: {1}] Document successfully updated.", messageID, ADDRESS);
              }
            });
          } catch (Exception e) {
            lock.release();
          }
        }
      });
    }
  }

}
