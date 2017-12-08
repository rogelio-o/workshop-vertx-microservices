package com.rogelioorts.workshop.vertx.microservices.scafolder;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rogelioorts.workshop.vertx.microservices.scafolder.services.ConfigurationService;
import com.rogelioorts.workshop.vertx.microservices.scafolder.services.DiscoveryService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.HttpEndpoint;

public abstract class BaseApplication extends AbstractVerticle {

  private final Logger log = LoggerFactory.getLogger(BaseApplication.class);

  private Record publishedRecord;

  protected abstract String getServiceName();

  protected abstract Router getRouter();

  @Override
  public void start(final Future<Void> start) {
    log.debug("Loading configuration...");
    ConfigurationService.start(vertx, confRes -> {
      if (confRes.failed()) {
        start.fail(confRes.cause());
      } else {
        try {
          final JsonObject conf = confRes.result();
          final String host = conf.getString("host");
          final int port = conf.getInteger("port", 0);

          log.debug("Running onConfigLodaded...");
          onConfigLoaded();

          log.debug("Configuring JSON parser...");
          configureJsonParser();

          createHttpServer(port, host, serverRes -> {
            if (serverRes.failed()) {
              start.fail(serverRes.cause());
            } else {
              final HttpServer server = serverRes.result();
              log.info("HTTP server listening on port " + server.actualPort());

              registerService(host, server.actualPort(), start);
            }
          });
        } catch (Exception e) {
          start.fail(e);
        }
      }
    });
  }

  @Override
  public void stop(final Future<Void> end) {
    ConfigurationService.stop();
    unregisterService(end);
  }

  protected void onConfigLoaded() {
    // Override if you want to do something after config is loaded (e.g. create database client).
    log.debug("No actions on onConfigLoaded...");
  }

  private void registerService(final String host, final int port, final Future<Void> future) {
    try {
      final String finalHost = host == null ? getDefaultHost() : host;
      log.debug("Registering service ({0}:{1})...", finalHost, String.valueOf(port));

      final Record record = HttpEndpoint.createRecord(getServiceName(), finalHost, port, "/");

      DiscoveryService.registerService(vertx, record, res -> {
        if (res.failed()) {
          future.fail(res.cause());
        } else {
          publishedRecord = res.result();
          future.complete();
        }
      });
    } catch (UnknownHostException e) {
      registerService("localhost", port, future);
    }
  }

  private void unregisterService(final Future<Void> end) {
    DiscoveryService.unregisterService(publishedRecord, end.completer());
  }

  private void configureJsonParser() {
    Json.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    Json.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    Json.mapper.setSerializationInclusion(Include.NON_NULL);
    Json.mapper.registerModule(new JavaTimeModule());
  }

  private void createHttpServer(final int port, final String host, final Handler<AsyncResult<HttpServer>> handler) {
    log.debug("Trying to start server in " + host + ":" + port);

    final HttpServer server = vertx.createHttpServer();
    server.requestHandler(getRouter()::accept).listen(port, handler);
  }

  private String getDefaultHost() throws UnknownHostException {
    final InetAddress localHost = InetAddress.getLocalHost();
    return localHost.getHostAddress();
  }

}
