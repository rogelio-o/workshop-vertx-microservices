package com.rogelioorts.workshop.vertx.microservices.series.episodes.routing.episodes;

import com.rogelioorts.workshop.vertx.microservices.scafolder.utils.Requests;
import com.rogelioorts.workshop.vertx.microservices.scafolder.utils.Responses;
import com.rogelioorts.workshop.vertx.microservices.series.episodes.models.Episode;
import com.rogelioorts.workshop.vertx.microservices.series.episodes.repositories.EpisodesRepository;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class CreateEpisodeHandler implements Handler<RoutingContext> {

  private final EpisodesRepository episodesRepository;

  public CreateEpisodeHandler(final EpisodesRepository episodesRepository) {
    this.episodesRepository = episodesRepository;
  }

  @Override
  public void handle(final RoutingContext context) {
    final String idSerie = context.pathParam("idSerie");

    Requests.bodyAsObjectAndValidate(context, Episode.class, model -> model.setIdSerie(idSerie), model -> {
      episodesRepository.insert(model, res -> {
        if (res.failed()) {
          context.fail(res.cause());
        } else {
          Responses.sendJson(context, model, HttpResponseStatus.CREATED.code());
        }
      });
    });
  }

}
