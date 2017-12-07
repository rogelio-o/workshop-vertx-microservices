package com.rogelioorts.workshop.vertx.microservices.series.episodes;

import com.rogelioorts.workshop.vertx.microservices.scafolder.BaseApplication;

import io.vertx.ext.web.Router;

public class Application extends BaseApplication {

  public static final String SERVICE_NAME = "series.episodes";

  @Override
  protected String getServiceName() {
    return SERVICE_NAME;
  }

  @Override
  protected Router getRouter() {
    return Router.router(vertx);
  }

}
