package com.rogelioorts.workshop.vertx.microservices.utils.services;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public final class ConfigurationService {
	
	private static JsonObject conf;
	
	private static ConfigRetriever retriever;

	private ConfigurationService() {
	}
	
	public static void start(Vertx vertx, Handler<AsyncResult<JsonObject>> handler) {
		ConfigStoreOptions file = new ConfigStoreOptions()
			.setType("file")
			.setFormat("json")
			.setConfig(new JsonObject().put("path", "application.json"));
		ConfigRetrieverOptions options = new ConfigRetrieverOptions()
			.setScanPeriod(2000)
			.addStore(file);
		
		ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
		
		retriever.listen(change -> {
			conf = change.getNewConfiguration();
		});
		
		retriever.getConfig(jsonResult -> {
			if(jsonResult.succeeded()) {
				conf = jsonResult.result();
				handler.handle(Future.succeededFuture(conf));
			} else {
				handler.handle(Future.failedFuture(jsonResult.cause()));
			}
		});
	}
	
	public static JsonObject getConf() {
		return conf;
	}
	
	public static void stop() {
		retriever.close();
	}
	
}
