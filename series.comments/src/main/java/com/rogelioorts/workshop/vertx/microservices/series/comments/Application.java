package com.rogelioorts.workshop.vertx.microservices.series.comments;

import com.rogelioorts.workshop.vertx.microservices.series.comments.controllers.CreateAction;
import com.rogelioorts.workshop.vertx.microservices.utils.BaseApplication;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;

public class Application extends BaseApplication {

  private static final String COMMENTS_PATH = "/api/v1/comments";

  public static final String SERVICE_NAME = "series.comments";

  @Override
  protected String getServiceName() {
    return SERVICE_NAME;
  }

  @Override
  protected Router getRouter() {
    Router router = Router.router(vertx);

    router.route(HttpMethod.POST, COMMENTS_PATH).handler(new CreateAction());

    return router;
  }

}
