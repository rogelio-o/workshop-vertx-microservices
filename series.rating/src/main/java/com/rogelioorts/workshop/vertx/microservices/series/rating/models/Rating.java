package com.rogelioorts.workshop.vertx.microservices.series.rating.models;

import java.time.LocalDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rogelioorts.workshop.vertx.microservices.scafolder.repositories.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rating implements Model {

  private static final int MIN_PUNTUATION = 0;
  private static final int MAX_PUNTUATION = 5;

  @JsonProperty("_id")
  private String id;

  @JsonProperty("id_serie")
  @NotEmpty
  private String idSerie;

  @NotNull
  @Min(MIN_PUNTUATION)
  @Max(MAX_PUNTUATION)
  private Integer puntuation;

  @JsonProperty("creation_date")
  private LocalDate creationDate;

}
