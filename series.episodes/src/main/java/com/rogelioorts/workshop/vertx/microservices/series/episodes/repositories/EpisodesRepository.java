package com.rogelioorts.workshop.vertx.microservices.series.episodes.repositories;

import com.rogelioorts.workshop.vertx.microservices.series.episodes.models.Episode;
import com.rogelioorts.workshop.vertx.microservices.utils.repositories.BaseRepository;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class EpisodesRepository extends BaseRepository<Episode> {

  public static final int DEFAULT_PER_PAGE = 20;

  private static final String COLLECTION = "episodes";

  public EpisodesRepository(final MongoClient client) {
    super(client, Episode.class);
  }

  @Override
  protected String getCollectionName() {
    return COLLECTION;
  }

  @Override
  protected int getDefaultResultsPerPage() {
    return DEFAULT_PER_PAGE;
  }

  @Override
  protected JsonObject getPaginationSort() {
    return new JsonObject().put("season", 1).put("number", 1);
  }

}
