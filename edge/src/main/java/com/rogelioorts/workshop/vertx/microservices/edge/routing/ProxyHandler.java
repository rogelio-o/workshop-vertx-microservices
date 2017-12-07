package com.rogelioorts.workshop.vertx.microservices.edge.routing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rogelioorts.workshop.vertx.microservices.scafolder.services.DiscoveryService;
import com.rogelioorts.workshop.vertx.microservices.scafolder.services.JsonClientResponse;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

public class ProxyHandler implements Handler<RoutingContext> {

  private final Logger log = LoggerFactory.getLogger(ProxyHandler.class);

  private final String service;

  private final HttpMethod method;

  private final String path;

  private final Matcher pathMatcher;

  public ProxyHandler(final String service, final HttpMethod method, final String path) {
    this.service = service;
    this.method = method;
    this.path = path;

    final Pattern pattern = Pattern.compile(":([a-zA-Z]+)");
    this.pathMatcher = pattern.matcher(path);
  }

  @Override
  public void handle(final RoutingContext context) {
    log.debug("Incoming request for " + method + " " + path);

    final String pathWithFilledPlaceholders = replacePathPlaceholders(context);

    try {
      final JsonObject body = context.getBodyAsJson();

      call(context, pathWithFilledPlaceholders, body);
    } catch (DecodeException e) {
      call(context, pathWithFilledPlaceholders, null);
    }
  }

  private void call(final RoutingContext context, final String pathWithFilledPlaceholders, final JsonObject body) {
    DiscoveryService.callJsonService(service, method, pathWithFilledPlaceholders, body, res -> {
      if (res.failed()) {
        context.fail(res.cause());
      } else {
        final JsonClientResponse jsonClientResponse = res.result();
        final HttpServerResponse response = context.response();

        jsonClientResponse.getClientResponse().headers().forEach(header -> response.putHeader(header.getKey(), header.getValue()));
        response.setStatusCode(jsonClientResponse.getClientResponse().statusCode());

        if (jsonClientResponse.getBody() == null) {
          response.end();
        } else {
          response.end(jsonClientResponse.getBody().toBuffer());
        }
      }
    });
  }

  private String replacePathPlaceholders(final RoutingContext context) {
    pathMatcher.reset();

    final StringBuffer result = new StringBuffer("");
    while (pathMatcher.find()) {
      final String key = pathMatcher.group(1);
      final String value = context.pathParam(key);

      log.debug("Replace in {0}: {1} for {2}.", path, key, value);
      pathMatcher.appendReplacement(result, value);
    }
    pathMatcher.appendTail(result);

    return result.toString();
  }

}