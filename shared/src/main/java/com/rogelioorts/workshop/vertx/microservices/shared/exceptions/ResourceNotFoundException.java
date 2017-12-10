package com.rogelioorts.workshop.vertx.microservices.shared.exceptions;

public class ResourceNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -9000420056613322991L;

  public ResourceNotFoundException(final String path) {
    super("Resource not found " + path);
  }

}
