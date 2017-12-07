package com.rogelioorts.workshop.vertx.microservices.series.episodes.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
  @NotEmpty
  private String idSerie;

  @NotNull
  private Integer season;

  @NotNull
  private Integer number;

  @NotEmpty
  private String name;

  private Long duration; // minutes

  @JsonProperty("release_date")
  private LocalDate releaseDate;

  @JsonProperty("creation_date")
  private LocalDateTime creationDate;

  @JsonProperty("update_date")
  private LocalDateTime updateDate;

}
