package com.rogelioorts.workshop.vertx.microservices.shared.services;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BufferClientResponse {

  private final HttpClientResponse clientResponse;

  private final Buffer body;

}
