package com.rogelioorts.workshop.vertx.microservices.series.rating.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingStatistics {

  private Double average;

  private Long total;

}
