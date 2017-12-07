package com.rogelioorts.workshop.vertx.microservices.series.episodes.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rogelioorts.workshop.vertx.microservices.scafolder.repositories.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Episode implements Model {

  @JsonProperty("_id")
  private String id;

  @JsonProperty("id_serie")
  private String idSerie;

  private int season;

  private int number;

  private String name;

  private long duration; // minutes

  @JsonProperty("release_date")
  private LocalDate releaseDate;

  @JsonProperty("creation_date")
  private LocalDateTime creationDate;

  @JsonProperty("update_date")
  private LocalDateTime updateDate;

}
