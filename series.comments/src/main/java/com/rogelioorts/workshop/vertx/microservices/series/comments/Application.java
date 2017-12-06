package com.rogelioorts.workshop.vertx.microservices.series.comments;

import com.rogelioorts.workshop.vertx.microservices.utils.BaseApplication;

import io.vertx.ext.web.Router;

public class Application extends BaseApplication {

  public static final String SERVICE_NAME = "series.comments";

  @Override
  protected String getServiceName() {
    return SERVICE_NAME;
  }

  @Override
  protected Router getRouter() {
    return Router.router(vertx);
  }

}
