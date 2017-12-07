package com.rogelioorts.workshop.vertx.microservices.scafolder.services;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public final class ConfigurationService {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationService.class);

  private static final int SCAN_PERIOD_IN_MS = 2000;

  private static JsonObject conf;

  private static ConfigRetriever retriever;

  private ConfigurationService() {
  }

  public static void start(final Vertx vertx, final Handler<AsyncResult<JsonObject>> handler) {
    final ConfigStoreOptions file = new ConfigStoreOptions().setType("file").setFormat("json").setConfig(new JsonObject().put("path", "application.json"));
    final ConfigRetrieverOptions options = new ConfigRetrieverOptions().setScanPeriod(SCAN_PERIOD_IN_MS).addStore(file);

    LOG.debug("Creating configuration retriever...");
    retriever = ConfigRetriever.create(vertx, options);

    retriever.listen(change -> {
      LOG.debug("Configuration has changed.");
      conf = change.getNewConfiguration();
    });

    LOG.debug("Retrieving initial configuration...");
    retriever.getConfig(jsonResult -> {
      if (jsonResult.succeeded()) {
        LOG.debug("Initial configuration retrieved.");
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
