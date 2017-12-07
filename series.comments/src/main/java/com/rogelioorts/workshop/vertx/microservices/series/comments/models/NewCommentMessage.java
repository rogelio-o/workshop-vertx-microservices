package com.rogelioorts.workshop.vertx.microservices.series.comments.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCommentMessage {

  private Comment comment;

  private long total;

}
