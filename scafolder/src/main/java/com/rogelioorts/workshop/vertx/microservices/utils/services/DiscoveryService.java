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
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

public final class DiscoveryService {

  private static final int MAX_FAILURES = 5;
  private static final long TIMEOUT = 2000;
  private static final long RESET_TIMEOUT = 10000;

  private static Object mutex = new Object();

  private static ServiceDiscovery discovery;

  private static CircuitBreaker breaker;

  private DiscoveryService() {
  }

  private static void start(final Vertx vertx) {
    ServiceDiscovery savedDiscovery = discovery;
    if (savedDiscovery == null) {
      synchronized (mutex) {
        savedDiscovery = discovery;
        if (savedDiscovery == null) {
          discovery = ServiceDiscovery.create(vertx);
        }
      }
    }

    CircuitBreaker savedBreaker = breaker;
    if (savedBreaker == null) {
      synchronized (mutex) {
        savedBreaker = breaker;
        if (savedBreaker == null) {
          breaker = CircuitBreaker.create("mcircuit-breaker", vertx,
              new CircuitBreakerOptions().setMaxFailures(MAX_FAILURES).setTimeout(TIMEOUT).setResetTimeout(RESET_TIMEOUT));
        }
      }
    }
  }

  public static void registerService(final Vertx vertx, final Record record, final Handler<AsyncResult<Record>> handler) {
    start(vertx);
    discovery.publish(record, handler);
  }

  public static void unregisterService() {
    discovery.close();
  }

  public static void getService(final String service, final Handler<AsyncResult<ServiceReference>> handler) {
    discovery.getRecords(new JsonObject().put("name", service), ar -> {
      if (ar.failed()) {
        handler.handle(Future.failedFuture(ar.cause()));
      } else {
        final List<Record> results = ar.result();

        if (results.isEmpty()) {
          handler.handle(Future.failedFuture(new NoSuchElementException()));
        } else {
          final Record record = results.get(0);
          handler.handle(Future.succeededFuture(discovery.getReference(record)));
        }
      }
    });
  }

  public static void callService(final String service, final HttpMethod method, final String path, final Handler<AsyncResult<Buffer>> handler) {
    final JsonObject recordQuery = new JsonObject().put("name", service);
    discovery.getRecord(recordQuery, recordResult -> {
      if (recordResult.failed()) {
        handler.handle(Future.failedFuture(recordResult.cause()));
      } else if (recordResult.result() == null) {
        handler.handle(Future.failedFuture(new NoSuchElementException("No service found with name " + service)));
      } else {
        final ServiceReference reference = discovery.getReference(recordResult.result());
        final HttpClient client = reference.getAs(HttpClient.class);

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

  public static void callJsonService(final String service, final HttpMethod method, final String path, final Handler<AsyncResult<JsonObject>> handler) {
    callService(service, method, path, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().toJsonObject()));
      }
    });
  }

}
