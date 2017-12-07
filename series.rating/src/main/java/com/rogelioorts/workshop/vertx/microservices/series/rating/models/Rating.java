package com.rogelioorts.workshop.vertx.microservices.series.rating.models;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rogelioorts.workshop.vertx.microservices.utils.repositories.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rating implements Model {

  @JsonProperty("_id")
  private String id;

  @JsonProperty("id_serie")
  @NotEmpty
  private String idSerie;

  private int puntuation;

  @JsonProperty("creation_date")
  private LocalDate creationDate;

}
