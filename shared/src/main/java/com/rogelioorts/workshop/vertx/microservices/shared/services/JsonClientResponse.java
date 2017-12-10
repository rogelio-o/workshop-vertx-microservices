package com.rogelioorts.workshop.vertx.microservices.shared.services;

import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JsonClientResponse {

  private final HttpClientResponse clientResponse;

  private final JsonObject body;

}
