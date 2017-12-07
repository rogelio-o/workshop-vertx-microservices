package com.rogelioorts.workshop.vertx.microservices.edge;

import com.rogelioorts.workshop.vertx.microservices.edge.controllers.comments.CreateCommentsAction;
import com.rogelioorts.workshop.vertx.microservices.edge.exceptions.ExceptionHandler;
import com.rogelioorts.workshop.vertx.microservices.scafolder.BaseApplication;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;

public class Application extends BaseApplication {

    public static final String SERVICE_NAME = "edge";

    @Override
    protected String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    protected Router getRouter() {
        final Router router = Router.router(vertx);
        final ExceptionHandler exceptionHandler = new ExceptionHandler();

        router.route(HttpMethod.GET, CreateCommentsAction.PATH).handler(new CreateCommentsAction()).failureHandler(exceptionHandler);

        return router;
    }

}
