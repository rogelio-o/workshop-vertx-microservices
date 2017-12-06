package com.rogelioorts.workshop.vertx.microservices.utils.repositories;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedOption {

  private int page; // First is 1

  private Integer perPage;

  @JsonIgnore
  public int getSkip() {
    return (page - 1) * perPage;
  }

}
