package com.rogelioorts.workshop.vertx.microservices.shared.utils;

import java.util.Set;
import java.util.function.Consumer;

import javax.validation.ConstraintViolation;

import com.rogelioorts.workshop.vertx.microservices.shared.repositories.ModelsValidator;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public final class Requests {

  private Requests() {
  }

  public static <T> void bodyAsObject(final RoutingContext context, final Class<T> bodyClass, final Handler<T> handler) {
    bodyAsObject(context, bodyClass, false, null, handler);
  }

  public static <T> void bodyAsObjectAndValidate(final RoutingContext context, final Class<T> bodyClass, final Handler<T> handler) {
    bodyAsObject(context, bodyClass, true, null, handler);
  }

  public static <T> void bodyAsObjectAndValidate(final RoutingContext context, final Class<T> bodyClass, final Consumer<T> transformer,
      final Handler<T> handler) {
    bodyAsObject(context, bodyClass, true, transformer, handler);
  }

  private static <T> void bodyAsObject(final RoutingContext context, final Class<T> bodyClass, final boolean validate, final Consumer<T> transformer,
      final Handler<T> handler) {
    final JsonObject body = context.getBodyAsJson();
    if (body == null) {
      context.fail(HttpResponseStatus.BAD_REQUEST.code());
    } else {
      final T model = body.mapTo(bodyClass);
      if (transformer != null) {
        transformer.accept(model);
      }

      if (validate) {
        final Set<ConstraintViolation<T>> validationErrors = ModelsValidator.validate(model);
        if (validationErrors.isEmpty()) {
          handler.handle(model);
        } else {
          Responses.sendJsonFormErrors(context, validationErrors);
        }
      } else {
        handler.handle(model);
      }
    }
  }

}
