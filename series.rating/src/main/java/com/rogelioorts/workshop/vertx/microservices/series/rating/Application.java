package com.rogelioorts.workshop.vertx.microservices.series.rating;

import com.rogelioorts.workshop.vertx.microservices.series.rating.repositories.RatingsRepository;
import com.rogelioorts.workshop.vertx.microservices.shared.BaseApplication;
import com.rogelioorts.workshop.vertx.microservices.shared.repositories.BaseRepository;

import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;

public class Application extends BaseApplication {

  private static final String RATING_PATH = "/api/v1/series/:idSerie/rating";

  public static final String SERVICE_NAME = "series.rating";

  // #PLACEHOLDER-18

  @Override
  protected String getServiceName() {
    return SERVICE_NAME;
  }

  @Override
  protected Router getRouter() {
    final MongoClient client = BaseRepository.createClient(vertx);
    final RatingsRepository ratingsRepository = new RatingsRepository(vertx, client);

    final Router router = null; // REPLACE WITH #PLACEHOLDER-24a

    return router;
  }

}
