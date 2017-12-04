package com.rogelioorts.workshop.vertx.microservices.utils.utils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public final class HandlersUtils {

	private HandlersUtils() {
	}
	
	public static <T> Handler<AsyncResult<T>> fromVoidHandler(Handler<AsyncResult<Void>> handle) {
		return ar -> {
			if(ar.succeeded()) {
				handle.handle(Future.succeededFuture());
			} else {
				handle.handle(Future.failedFuture(ar.cause()));
			}
		};
	}
	
}
