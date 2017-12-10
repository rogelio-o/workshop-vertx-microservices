package com.rogelioorts.workshop.vertx.microservices.shared.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.validation.ConstraintViolation;

import com.rogelioorts.workshop.vertx.microservices.shared.repositories.ModelsValidator;
import com.rogelioorts.workshop.vertx.microservices.shared.repositories.PaginatedResult;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public final class Responses {

  private Responses() {
  }

  public static <T> void sendJsonFormErrors(final RoutingContext context, final Set<ConstraintViolation<T>> validationErrors) {
    sendJson(context, ModelsValidator.getErrorBody(validationErrors), HttpResponseStatus.BAD_REQUEST.code());
  }

  public static <T> void sendJson(final RoutingContext context, final T objResponse) {
    sendJson(context, objResponse, HttpResponseStatus.OK.code());
  }

  public static <T> void sendJson(final RoutingContext context, final PaginatedResult<T> paginatedResult) {
    final JsonArray results = new JsonArray();
    paginatedResult.getResults().forEach(obj -> results.add(JsonObject.mapFrom(obj)));

    final JsonObject jsonResponse = new JsonObject().put("results", results).put("page", paginatedResult.getPage())
        .put("per_page", paginatedResult.getPerPage()).put("total_pages", paginatedResult.getTotalPages())
        .put("total_results", paginatedResult.getTotalResults());

    sendJson(context, jsonResponse, HttpResponseStatus.OK.code());
  }

  public static <T> void sendJson(final RoutingContext context, final T objResponse, final int statusCode) {
    sendJson(context, JsonObject.mapFrom(objResponse), statusCode);
  }

  public static <T> void sendJson(final RoutingContext context, final JsonObject jsonResponse, final int statusCode) {
    final HttpServerResponse response = context.response();
    final Buffer bufferResponse = jsonResponse.toBuffer();
    final Charset charset = StandardCharsets.UTF_8;

    response.putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=" + charset.name());
    response.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(bufferResponse.length()));
    response.setStatusCode(statusCode);

    response.end(bufferResponse.toString(charset));
  }

}
