package com.rogelioorts.workshop.vertx.microservices.series.episodes.routing.episodes;

import java.util.NoSuchElementException;

import com.rogelioorts.workshop.vertx.microservices.series.episodes.repositories.EpisodesRepository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class DeleteEpisodeHandler implements Handler<RoutingContext> {

  private final EpisodesRepository episodesRepository;

  public DeleteEpisodeHandler(final EpisodesRepository episodesRepository) {
    this.episodesRepository = episodesRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    final String id = context.pathParam("id");

    episodesRepository.delete(id, res -> {
      if (res.failed()) {
        final Throwable exception = res.cause();

        if (exception.getClass().equals(NoSuchElementException.class)) {
          sendOkResponse(context);
        } else {
          context.fail(res.cause());
        }
      } else {
        sendOkResponse(context);
      }
    });
  }

  private void sendOkResponse(final RoutingContext context) {
    context.response().setStatusCode(HttpResponseStatus.NO_CONTENT.code()).end();
  }

}
