package com.rogelioorts.workshop.vertx.microservices.edge.routing;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.PebbleTemplateEngine;

public class MainHandler implements Handler<RoutingContext> {

  private static final String TEMPLATE_PATH = "templates/";
  private static final String INDEX_TEMPLATE = "index";

  private final PebbleTemplateEngine templateEngine;

  public MainHandler(final PebbleTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  @Override
  public void handle(final RoutingContext context) {
    templateEngine.render(context, TEMPLATE_PATH, INDEX_TEMPLATE, res -> {
      if (res.failed()) {
        context.fail(res.cause());
      } else {
        final Charset encode = StandardCharsets.UTF_8;
        final Buffer buffer = res.result();

        final HttpServerResponse response = context.response();
        response.setStatusCode(HttpResponseStatus.OK.code());
        response.putHeader(HttpHeaders.CONTENT_TYPE, "text/html; encode=" + encode.name());
        response.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(buffer.length()));
        response.end(buffer.toString(encode));
      }
    });
  }

}
