package com.rogelioorts.workshop.vertx.microservices.series.comments;

import com.rogelioorts.workshop.vertx.microservices.scafolder.BaseApplication;
import com.rogelioorts.workshop.vertx.microservices.scafolder.exceptions.JsonExceptionHandler;
import com.rogelioorts.workshop.vertx.microservices.scafolder.exceptions.ResourceNotFoundHandler;
import com.rogelioorts.workshop.vertx.microservices.scafolder.repositories.BaseRepository;
import com.rogelioorts.workshop.vertx.microservices.series.comments.repositories.CommentsRepository;
import com.rogelioorts.workshop.vertx.microservices.series.comments.routing.comments.CreateCommentHandler;
import com.rogelioorts.workshop.vertx.microservices.series.comments.routing.comments.DeleteCommentHandler;
import com.rogelioorts.workshop.vertx.microservices.series.comments.routing.comments.ListCommentsHandler;
import com.rogelioorts.workshop.vertx.microservices.series.comments.routing.comments.UpdateCommentHandler;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class Application extends BaseApplication {

  private static final String COMMENTS_PATH = "/api/v1/series/:idSerie/comments";

  public static final String SERVICE_NAME = "series.comments";

  @Override
  protected String getServiceName() {
    return SERVICE_NAME;
  }

  @Override
  protected Router getRouter() {
    final MongoClient client = BaseRepository.createClient(vertx);
    final CommentsRepository commentsRepository = new CommentsRepository(vertx, client);

    final Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route(HttpMethod.GET, COMMENTS_PATH).handler(new ListCommentsHandler(commentsRepository));
    router.route(HttpMethod.POST, COMMENTS_PATH).handler(new CreateCommentHandler(commentsRepository));
    router.route(HttpMethod.PUT, COMMENTS_PATH + "/:id").handler(new UpdateCommentHandler(commentsRepository));
    router.route(HttpMethod.DELETE, COMMENTS_PATH + "/:id").handler(new DeleteCommentHandler(commentsRepository));

    router.route().handler(new ResourceNotFoundHandler()).failureHandler(new JsonExceptionHandler());

    return router;
  }

}
