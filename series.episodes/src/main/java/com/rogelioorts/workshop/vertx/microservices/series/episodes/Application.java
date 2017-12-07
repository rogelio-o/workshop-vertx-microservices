package com.rogelioorts.workshop.vertx.microservices.series.episodes;

import com.rogelioorts.workshop.vertx.microservices.scafolder.BaseApplication;
import com.rogelioorts.workshop.vertx.microservices.scafolder.exceptions.JsonExceptionHandler;
import com.rogelioorts.workshop.vertx.microservices.scafolder.repositories.BaseRepository;
import com.rogelioorts.workshop.vertx.microservices.series.episodes.repositories.EpisodesRepository;
import com.rogelioorts.workshop.vertx.microservices.series.episodes.routing.episodes.CreateEpisodeHandler;
import com.rogelioorts.workshop.vertx.microservices.series.episodes.routing.episodes.DeleteEpisodeHandler;
import com.rogelioorts.workshop.vertx.microservices.series.episodes.routing.episodes.ListEpisodesHandler;
import com.rogelioorts.workshop.vertx.microservices.series.episodes.routing.episodes.UpdateEpisodeHandler;
import com.rogelioorts.workshop.vertx.microservices.series.episodes.routing.episodes.ViewEpisodeHandler;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class Application extends BaseApplication {

  private static final String EPISODES_PATH = "/api/v1/series/:idSerie/episodes";

  public static final String SERVICE_NAME = "series.episodes";

  @Override
  protected String getServiceName() {
    return SERVICE_NAME;
  }

  @Override
  protected Router getRouter() {
    final MongoClient client = BaseRepository.createClient(vertx);
    final EpisodesRepository episodesRepository = new EpisodesRepository(client);

    final Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route(HttpMethod.GET, EPISODES_PATH + "/:id").handler(new ViewEpisodeHandler(episodesRepository));
    router.route(HttpMethod.GET, EPISODES_PATH).handler(new ListEpisodesHandler(episodesRepository));
    router.route(HttpMethod.POST, EPISODES_PATH).handler(new CreateEpisodeHandler(episodesRepository));
    router.route(HttpMethod.PUT, EPISODES_PATH + "/:id").handler(new UpdateEpisodeHandler(episodesRepository));
    router.route(HttpMethod.DELETE, EPISODES_PATH + "/:id").handler(new DeleteEpisodeHandler(episodesRepository));

    router.route().failureHandler(new JsonExceptionHandler());

    return router;
  }

}
