package com.rogelioorts.workshop.vertx.microservices.series.rating;

import com.rogelioorts.workshop.vertx.microservices.utils.BaseApplication;

import io.vertx.ext.web.Router;

public class Application extends BaseApplication {
	
	public final static String SERVICE_NAME = "series.episodes";

	protected String getServiceName() {
		return SERVICE_NAME;
	}
	
	protected Router getRouter() {
		Router router = Router.router(vertx);
		
		return router;
	}
	
}
