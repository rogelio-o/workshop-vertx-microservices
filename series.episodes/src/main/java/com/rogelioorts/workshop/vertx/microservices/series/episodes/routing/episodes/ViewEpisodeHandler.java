package com.rogelioorts.workshop.vertx.microservices.series.episodes.routing.episodes;

import com.rogelioorts.workshop.vertx.microservices.scafolder.utils.Responses;
import com.rogelioorts.workshop.vertx.microservices.series.episodes.models.Episode;
import com.rogelioorts.workshop.vertx.microservices.series.episodes.repositories.EpisodesRepository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class ViewEpisodeHandler implements Handler<RoutingContext> {

  private final EpisodesRepository episodesRepository;

  public ViewEpisodeHandler(final EpisodesRepository episodesRepository) {
    this.episodesRepository = episodesRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    final String id = context.pathParam("id");

    episodesRepository.find(id, res -> {
      if (res.failed()) {
        context.fail(res.cause());
      } else {
        final Episode model = res.result();

        if (model == null) {
          context.fail(HttpResponseStatus.NOT_FOUND.code());
        } else {
          Responses.sendJson(context, model);
        }
      }
    });
  }

}
