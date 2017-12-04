package com.rogelioorts.workshop.vertx.microservices.utils.services;

import java.util.List;
import java.util.NoSuchElementException;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;

public final class DiscoveryService {
	
	private static ServiceDiscovery discovery;

	private DiscoveryService() {
	}
	
	private static void start(Vertx vertx) {
		if(discovery == null) {
			discovery = ServiceDiscovery.create(vertx);
		}
	}
	
	public static void registerService(Vertx vertx, Record record, Handler<AsyncResult<Record>> handler) {
		start(vertx);
		discovery.publish(record, handler);
	}
	
	public static void unregisterService() {
		discovery.close();
	}
	
	public static void getService(String service, Handler<AsyncResult<ServiceReference>> handler) {
		discovery.getRecords(new JsonObject().put("name", service), ar -> {
			if(ar.failed()) {
				handler.handle(Future.failedFuture(ar.cause()));
			} else {
				List<Record> results = ar.result();
				
				if(results.isEmpty()) {
					handler.handle(Future.failedFuture(new NoSuchElementException()));
				} else {
					Record record = results.get(0);
					handler.handle(Future.succeededFuture(discovery.getReference(record)));
				}
			}
		});
	}
	
}
