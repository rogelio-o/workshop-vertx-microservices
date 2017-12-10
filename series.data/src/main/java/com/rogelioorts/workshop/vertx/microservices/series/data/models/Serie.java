package com.rogelioorts.workshop.vertx.microservices.series.data.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rogelioorts.workshop.vertx.microservices.shared.repositories.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Serie implements Model {

  @JsonProperty("_id")
  private String id;

  @NotEmpty
  private String name;

  private String description;

  @NotEmpty
  private String channel;

  @JsonProperty("start_date")
  private LocalDate startDate;

  @NotNull
  private SerieStatus status;

  private List<String> cast;

  private List<String> tags;

  private String image;

  @JsonProperty("num_comments")
  private long numComments;

  @JsonProperty("num_ratings")
  private long numRatings;

  @JsonProperty("average_rating")
  private double averageRating;

  @JsonProperty("creation_date")
  private LocalDateTime creationDate;

  @JsonProperty("update_date")
  private LocalDateTime updateDate;

}
