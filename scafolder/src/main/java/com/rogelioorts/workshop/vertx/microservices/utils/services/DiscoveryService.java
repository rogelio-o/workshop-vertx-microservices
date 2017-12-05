package com.rogelioorts.workshop.vertx.microservices.utils.services;

import java.util.List;
import java.util.NoSuchElementException;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;

public final class DiscoveryService {
	
	private static ServiceDiscovery discovery;
	
	private static CircuitBreaker breaker;

	private DiscoveryService() {
	}
	
	private static void start(Vertx vertx) {
		if(discovery == null) {
			discovery = ServiceDiscovery.create(vertx);
		}
		
		if(breaker == null) {
			breaker = CircuitBreaker.create("mcircuit-breaker", vertx, new CircuitBreakerOptions()
		        .setMaxFailures(5)
		        .setTimeout(2000)
		        .setResetTimeout(10000)
		    );
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
	
	public static void callService(String service, HttpMethod method, String path, Handler<AsyncResult<Buffer>> handler) {
		JsonObject recordQuery = new JsonObject().put("name", service);
		discovery.getRecord(recordQuery, recordResult -> {
			if(recordResult.failed()) {
				handler.handle(Future.failedFuture(recordResult.cause()));
			} else if(recordResult.result() == null) {
				handler.handle(Future.failedFuture(new NoSuchElementException("No service found with name " + service)));
			} else {
				ServiceReference reference = discovery.getReference(recordResult.result());
				HttpClient client = reference.getAs(HttpClient.class);
				
				breaker.<Buffer>execute(future -> {
					client.request(method, path, httpClient -> {
						httpClient.exceptionHandler(error -> future.fail(error));
						httpClient.endHandler(v -> client.close());
						httpClient.bodyHandler(buffer -> future.complete(buffer));
					});
				}).setHandler(handler);
			}
		});
	}
	
	public static void callJsonService(String service, HttpMethod method, String path, Handler<AsyncResult<JsonObject>> handler) {
		callService(service, method, path, res -> {
			if(res.failed()) {
				handler.handle(Future.failedFuture(res.cause()));
			} else {
				handler.handle(Future.succeededFuture(res.result().toJsonObject()));
			}
		});
	}
	
}
