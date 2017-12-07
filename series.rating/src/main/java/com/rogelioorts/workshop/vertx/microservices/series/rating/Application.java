package com.rogelioorts.workshop.vertx.microservices.series.rating;

import com.rogelioorts.workshop.vertx.microservices.series.rating.controllers.CreateController;
import com.rogelioorts.workshop.vertx.microservices.series.rating.repositories.RatingsRepository;
import com.rogelioorts.workshop.vertx.microservices.utils.BaseApplication;
import com.rogelioorts.workshop.vertx.microservices.utils.repositories.BaseRepository;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class Application extends BaseApplication {

  private static final String RATING_PATH = "/api/v1/:idSerie/rating";

  public static final String SERVICE_NAME = "series.episodes";

  @Override
  protected String getServiceName() {
    return SERVICE_NAME;
  }

  @Override
  protected Router getRouter() {
    final MongoClient client = BaseRepository.createClient(vertx);
    final RatingsRepository ratingsRepository = new RatingsRepository(vertx, client);

    final Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route(HttpMethod.POST, RATING_PATH).handler(new CreateController(ratingsRepository));

    return router;
  }

}
