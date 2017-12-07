package com.rogelioorts.workshop.vertx.microservices.utils.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;

public final class DiscoveryService {

  private static final int MAX_FAILURES = 5;
  private static final long TIMEOUT = 10000;
  private static final long RESET_TIMEOUT = 10000;

  private static Object mutex = new Object();

  private static ServiceDiscovery discovery;

  private static CircuitBreaker breaker;

  private static final ConcurrentMap<String, AtomicInteger> SERVICES_BALANCER = new ConcurrentHashMap<>();

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

  public static void unregisterService(final Record record, final Handler<AsyncResult<Void>> handler) {
    discovery.unpublish(record.getRegistration(), res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        discovery.close();
        handler.handle(Future.succeededFuture());
      }
    });
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
    callService(service, method, path, null, handler);
  }

  public static void callService(final String service, final HttpMethod method, final String path, final Buffer body,
      final Handler<AsyncResult<Buffer>> handler) {
    final JsonObject recordQuery = new JsonObject().put("name", service);
    discovery.getRecords(recordQuery, recordResult -> {
      if (recordResult.failed()) {
        handler.handle(Future.failedFuture(recordResult.cause()));
      } else {
        try {
          final ServiceReference reference = getBalancedReference(service, recordResult.result());

          final HttpClient client = reference.getAs(HttpClient.class);

          breaker.<Buffer>execute(future -> {
            final HttpClientRequest request = client.request(method, path, httpClient -> {
              httpClient.exceptionHandler(error -> future.fail(error));
              httpClient.endHandler(v -> client.close());
              httpClient.bodyHandler(buffer -> future.complete(buffer));
            });

            request.exceptionHandler(error -> future.fail(error));

            if (body == null) {
              request.end();
            } else {
              request.end(body);
            }
          }).setHandler(handler);
        } catch (NoSuchElementException e) {
          handler.handle(Future.failedFuture(e));
        }
      }
    });
  }

  private static ServiceReference getBalancedReference(final String service, final List<Record> records) throws NoSuchElementException {
    if (records == null || records.isEmpty()) {
      throw new NoSuchElementException("No service found with name " + service);
    } else {
      SERVICES_BALANCER.putIfAbsent(service, new AtomicInteger(0));
      final AtomicInteger atomicIndex = SERVICES_BALANCER.get(service);
      final int index = atomicIndex.getAndUpdate(v -> v >= (records.size() - 1) ? 0 : v + 1);

      return discovery.getReference(records.get(index));
    }
  }

  public static void callJsonService(final String service, final HttpMethod method, final String path, final Handler<AsyncResult<JsonObject>> handler) {
    callJsonService(service, method, path, handler);
  }

  public static void callJsonService(final String service, final HttpMethod method, final String path, final JsonObject body,
      final Handler<AsyncResult<JsonObject>> handler) {
    Buffer bodyAsBuffer = null;
    if (body != null) {
      bodyAsBuffer = body.toBuffer();
    }

    callService(service, method, path, bodyAsBuffer, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        handler.handle(Future.succeededFuture(res.result().toJsonObject()));
      }
    });
  }

}
