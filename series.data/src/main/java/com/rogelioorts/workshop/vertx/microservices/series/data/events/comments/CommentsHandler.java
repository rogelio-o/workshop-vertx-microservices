package com.rogelioorts.workshop.vertx.microservices.series.data.events.comments;

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

public class CommentsHandler implements Handler<Message<JsonObject>> {

  public static final String NEW_ADDRESS = "new.comment";

  public static final String REMOVE_ADDRESS = "remove.comment";

  private static final String LOCK_PREFIX = "comment.";

  private final Logger log = LoggerFactory.getLogger(CommentsHandler.class);

  private final Vertx vertx;

  private final SeriesRepository seriesRepository;

  public CommentsHandler(final Vertx vertx, final SeriesRepository seriesRepository) {
    this.vertx = vertx;
    this.seriesRepository = seriesRepository;
  }

  @Override
  public void handle(final Message<JsonObject> message) {
    final JsonObject body = message.body();
    final LocalDateTime msgReceivedDate = LocalDateTime.now();
    final String messageID = UUID.randomUUID().toString();

    log.info("BUS MESSAGE [ID: {0}, ADDRESS: {1}] Received at {2}: {3}.", messageID, message.address(), msgReceivedDate, body);

    final String idSerie = body == null ? null : body.getString("id_serie", null);

    if (idSerie == null) {
      log.warn("BUS MESSAGE [ID: {0}, ADDRESS: {1}] There is no id_serie into the message.", messageID, message.address());
    } else {
      final SharedData sharedDate = vertx.sharedData();
      sharedDate.getLock(LOCK_PREFIX + idSerie, lockRes -> {
        if (lockRes.failed()) {
          log.error("BUS MESSAGE [ID: {0}, ADDRESS: {1}] Error getting lock.", messageID, message.address(), lockRes.cause());
        } else {
          final Lock lock = lockRes.result();

          try {
            final long total = body.getLong("total", 0L);

            seriesRepository.findAndUpdateCommentsStatisticsIfNotUpdatedIfNotUpdated(idSerie, msgReceivedDate, total, res -> {
              lock.release();

              if (res.failed()) {
                final Throwable exception = res.cause();

                if (IllegalArgumentException.class.equals(exception.getClass())) {
                  log.info("BUS MESSAGE [ID: {0}, ADDRESS: {1}] Previously updated.", messageID, message.address());
                } else {
                  log.error("BUS MESSAGE [ID: {0}, ADDRESS: {1}] Error updating document.", messageID, message.address(), res.cause());
                }
              } else {
                log.info("BUS MESSAGE [ID: {0}, ADDRESS: {1}] Document successfully updated.", messageID, message.address());
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
