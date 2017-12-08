package com.rogelioorts.workshop.vertx.microservices.series.data;

import com.rogelioorts.workshop.vertx.microservices.scafolder.BaseApplication;
import com.rogelioorts.workshop.vertx.microservices.scafolder.exceptions.JsonExceptionHandler;
import com.rogelioorts.workshop.vertx.microservices.scafolder.exceptions.ResourceNotFoundHandler;
import com.rogelioorts.workshop.vertx.microservices.scafolder.repositories.BaseRepository;
import com.rogelioorts.workshop.vertx.microservices.series.data.events.comments.CommentsHandler;
import com.rogelioorts.workshop.vertx.microservices.series.data.events.rating.NewRatingHandler;
import com.rogelioorts.workshop.vertx.microservices.series.data.repositories.SeriesRepository;
import com.rogelioorts.workshop.vertx.microservices.series.data.routing.series.CreateSerieHandler;
import com.rogelioorts.workshop.vertx.microservices.series.data.routing.series.DeleteSerieHandler;
import com.rogelioorts.workshop.vertx.microservices.series.data.routing.series.ListSeriesHandler;
import com.rogelioorts.workshop.vertx.microservices.series.data.routing.series.UpdateSerieHandler;
import com.rogelioorts.workshop.vertx.microservices.series.data.routing.series.ViewSerieHandler;

import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class Application extends BaseApplication {

  private static final String SERIES_PATH = "/api/v1/series";

  public static final String SERVICE_NAME = "series.data";

  private SeriesRepository seriesRepository;

  @Override
  protected String getServiceName() {
    return SERVICE_NAME;
  }

  @Override
  protected Router getRouter() {
    final Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route(HttpMethod.GET, SERIES_PATH + "/:id").handler(new ViewSerieHandler(seriesRepository));
    router.route(HttpMethod.GET, SERIES_PATH).handler(new ListSeriesHandler(seriesRepository));
    router.route(HttpMethod.POST, SERIES_PATH).handler(new CreateSerieHandler(seriesRepository));
    router.route(HttpMethod.PUT, SERIES_PATH + "/:id").handler(new UpdateSerieHandler(seriesRepository));
    router.route(HttpMethod.DELETE, SERIES_PATH + "/:id").handler(new DeleteSerieHandler(seriesRepository));

    router.route().handler(new ResourceNotFoundHandler()).failureHandler(new JsonExceptionHandler());

    return router;
  }

  @Override
  protected void onConfigLoaded() {
    final MongoClient client = BaseRepository.createClient(vertx);
    this.seriesRepository = new SeriesRepository(client);

    addEvenBusListeners();
  }

  private void addEvenBusListeners() {
    final EventBus eventBus = vertx.eventBus();

    final CommentsHandler commentsHandler = new CommentsHandler(vertx, seriesRepository);
    eventBus.consumer(CommentsHandler.NEW_ADDRESS, commentsHandler);
    eventBus.consumer(CommentsHandler.REMOVE_ADDRESS, commentsHandler);

    eventBus.consumer(NewRatingHandler.ADDRESS, new NewRatingHandler(vertx, seriesRepository));
  }

}
