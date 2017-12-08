package com.rogelioorts.workshop.vertx.microservices.series.rating.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingStatistics {

  @JsonProperty("id_serie")
  private String idSerie;

  private Double average;

  private Long total;

}
