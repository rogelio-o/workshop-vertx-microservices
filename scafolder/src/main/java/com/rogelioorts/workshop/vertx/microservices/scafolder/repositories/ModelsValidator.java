package com.rogelioorts.workshop.vertx.microservices.scafolder.repositories;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.metadata.ConstraintDescriptor;

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
    final JsonArray result = new JsonArray();

    violations.forEach(violation -> {
      final ModelError modelError = new ModelError();
      modelError.setAttribute(violation.getPropertyPath().toString());
      modelError.setCode(violation.getMessageTemplate());
      modelError.setMessage(violation.getMessage());
      final ConstraintDescriptor<?> descriptor = violation.getConstraintDescriptor();
      if (descriptor != null && descriptor.getAttributes() != null) {
        final Map<String, Object> params = descriptor.getAttributes().entrySet().stream().filter(ModelsValidator::filterConstraintParams)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (!params.isEmpty()) {
          modelError.setParams(params);
        }
      }

      result.add(JsonObject.mapFrom(modelError));
    });

    return result;
  }

  private static boolean filterConstraintParams(final Map.Entry<String, Object> entry) {
    final String key = entry.getKey();
    return !("groups".equals(key) || "payload".equals(key) || "message".equals(key));
  }

  public static <T> JsonObject getErrorBody(final Set<ConstraintViolation<T>> violations) {
    return new JsonObject().put("errors", violationsToBodyErrors(violations));
  }

}
