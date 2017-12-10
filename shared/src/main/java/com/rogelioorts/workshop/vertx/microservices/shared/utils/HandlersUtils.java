package com.rogelioorts.workshop.vertx.microservices.shared.utils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public final class HandlersUtils {

  private HandlersUtils() {
  }

  public static <T> Handler<AsyncResult<T>> fromVoidHandler(final Handler<AsyncResult<Void>> handler) {
    return ar -> {
      if (ar.succeeded()) {
        handler.handle(Future.succeededFuture());
      } else {
        handler.handle(Future.failedFuture(ar.cause()));
      }
    };
  }

  public static <T> Handler<T> wrapWithAsyncResult(final Handler<AsyncResult<T>> handler) {
    return result -> {
      handler.handle(Future.succeededFuture(result));
    };
  }

}
