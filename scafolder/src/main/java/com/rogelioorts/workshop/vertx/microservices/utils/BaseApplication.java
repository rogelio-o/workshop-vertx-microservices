package com.rogelioorts.workshop.vertx.microservices.utils;

import com.rogelioorts.workshop.vertx.microservices.utils.services.ConfigurationService;
import com.rogelioorts.workshop.vertx.microservices.utils.services.DiscoveryService;
import com.rogelioorts.workshop.vertx.microservices.utils.utils.HandlersUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpEndpoint;

public abstract class BaseApplication extends AbstractVerticle {
	
	protected abstract String getServiceName();
	
	protected abstract Router getRouter();

	@Override
	public void start(Future<Void> start) {
		ConfigurationService.start(vertx, confRes -> {
			if(confRes.failed()) {
				start.fail(confRes.cause());
			} else {
				JsonObject conf = confRes.result();
				int port = conf.getInteger("port", 0);
				String host = conf.getString("host", "localhost"); // TODO auto calculate hostname
				
				HttpServer server = vertx.createHttpServer();
				server.requestHandler(getRouter()::accept).listen(port, host, serverRes -> {
					if(serverRes.failed()) {
						start.fail(serverRes.cause());
					} else {
						registerService(host, server.actualPort(), start);
					}
				});
			}
		});
	}
	
	@Override
	public void stop() {
		DiscoveryService.unregisterService();
		ConfigurationService.stop();
	}
	
	private void registerService(String host, int port, Future<Void> start) {
		Record record = HttpEndpoint.createRecord(
			getServiceName(),
			host,
			port,
			"/api"
		);
		
		DiscoveryService.registerService(vertx, record, HandlersUtils.fromVoidHandler(start.completer()));
	}
	
}
