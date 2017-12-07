package com.rogelioorts.workshop.vertx.microservices.utils.repositories;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public final class ModelsValidator {

  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

  private ModelsValidator() {
  }

  public static <T> Set<ConstraintViolation<T>> validate(final T obj) {
    return VALIDATOR.validate(obj);
  }

  private static <T> JsonArray violationsToBodyErrors(final Set<ConstraintViolation<T>> violations) {
    JsonArray result = new JsonArray();

    violations.forEach(violation -> {
      result.add(violation.getMessage());
    });

    return result;
  }

  public static <T> JsonObject getErrorBody(final Set<ConstraintViolation<T>> violations) {
    return new JsonObject().put("errors", violationsToBodyErrors(violations));
  }

}
