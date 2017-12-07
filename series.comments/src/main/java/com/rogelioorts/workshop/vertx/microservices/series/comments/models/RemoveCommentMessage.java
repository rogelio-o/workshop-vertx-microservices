package com.rogelioorts.workshop.vertx.microservices.series.comments.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveCommentMessage {

  private String id;

  private long total;

}
