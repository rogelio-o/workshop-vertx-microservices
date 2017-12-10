package com.rogelioorts.workshop.vertx.microservices.series.episodes.routing.episodes;

import java.util.NoSuchElementException;

import com.rogelioorts.workshop.vertx.microservices.series.episodes.models.Episode;
import com.rogelioorts.workshop.vertx.microservices.series.episodes.repositories.EpisodesRepository;
import com.rogelioorts.workshop.vertx.microservices.shared.utils.Requests;
import com.rogelioorts.workshop.vertx.microservices.shared.utils.Responses;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class UpdateEpisodeHandler implements Handler<RoutingContext> {

  private final EpisodesRepository episodesRepository;

  public UpdateEpisodeHandler(final EpisodesRepository episodesRepository) {
    this.episodesRepository = episodesRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    final String id = context.pathParam("id");
    final String idSerie = context.pathParam("idSerie");

    Requests.bodyAsObjectAndValidate(context, Episode.class, model -> model.setIdSerie(idSerie), model -> {
      episodesRepository.update(id, model, res -> {
        if (res.failed()) {
          if (res.cause().getClass().equals(NoSuchElementException.class)) {
            context.fail(HttpResponseStatus.NOT_FOUND.code());
          } else {
            context.fail(res.cause());
          }
        } else {
          Responses.sendJson(context, res.result());
        }
      });
    });
  }

}
