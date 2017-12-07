package com.rogelioorts.workshop.vertx.microservices.utils.repositories;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.rogelioorts.workshop.vertx.microservices.utils.services.ConfigurationService;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

public abstract class BaseRepository<T extends Model> {

  private final MongoClient client;

  private final Class<T> modelClass;

  protected BaseRepository(final MongoClient client, final Class<T> modelClass) {
    this.client = client;
    this.modelClass = modelClass;
  }

  protected abstract String getCollectionName();

  protected abstract int getDefaultResultsPerPage();

  protected void beforeInsert(final T model, final Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.succeededFuture());
  }

  protected void afterInsert(final T model, final Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.succeededFuture());
  }

  public void insert(final T model, final Handler<AsyncResult<Void>> handler) {
    beforeInsert(model, beforeRes -> {
      if (beforeRes.failed()) {
        handler.handle(Future.failedFuture(beforeRes.cause()));
      } else {
        client.insert(getCollectionName(), JsonObject.mapFrom(model), res -> {
          if (res.failed()) {
            handler.handle(Future.failedFuture(res.cause()));
          } else {
            model.setId(res.result());

            afterInsert(model, afterRes -> {
              if (afterRes.failed()) {
                handler.handle(Future.failedFuture(afterRes.cause()));
              } else {
                handler.handle(Future.succeededFuture());
              }
            });
          }
        });
      }
    });
  }

  protected void beforeUpdate(final T model, final Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.succeededFuture());
  }

  protected void afterUpdate(final T model, final Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.succeededFuture());
  }

  public void update(final T model, final Handler<AsyncResult<Void>> handler) {
    beforeUpdate(model, beforeRes -> {
      if (beforeRes.failed()) {
        handler.handle(Future.failedFuture(beforeRes.cause()));
      } else {
        final JsonObject query = new JsonObject().put("_id", model.getId());
        client.findOneAndReplace(getCollectionName(), query, JsonObject.mapFrom(model), res -> {
          if (res.failed()) {
            handler.handle(Future.failedFuture(res.cause()));
          } else {
            afterUpdate(model, afterRes -> {
              if (afterRes.failed()) {
                handler.handle(Future.failedFuture(afterRes.cause()));
              } else {
                handler.handle(Future.succeededFuture());
              }
            });
          }
        });
      }
    });
  }

  protected void beforeDelete(final T model, final Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.succeededFuture());
  }

  protected void afterDelete(final T model, final Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.succeededFuture());
  }

  public void delete(final T model, final Handler<AsyncResult<Void>> handler) {
    beforeDelete(model, beforeRes -> {
      if (beforeRes.failed()) {
        handler.handle(Future.failedFuture(beforeRes.cause()));
      } else {
        final JsonObject query = new JsonObject().put("_id", model.getId());
        client.findOneAndDelete(getCollectionName(), query, res -> {
          if (res.failed()) {
            handler.handle(Future.failedFuture(res.cause()));
          } else {
            afterDelete(model, afterRes -> {
              if (afterRes.failed()) {
                handler.handle(Future.failedFuture(afterRes.cause()));
              } else {
                handler.handle(Future.succeededFuture());
              }
            });
          }
        });
      }
    });
  }

  protected abstract JsonObject getPaginationSort();

  public void findPaginated(final PaginatedOption paginatedOption, final String idSerie, final Handler<AsyncResult<PaginatedResult<T>>> handler) {
    if (paginatedOption.getPerPage() == null) {
      paginatedOption.setPerPage(getDefaultResultsPerPage());
    }

    final JsonObject query = new JsonObject().put("id_serie", idSerie);
    final JsonObject sort = getPaginationSort();
    final FindOptions options = new FindOptions().setSkip(paginatedOption.getSkip()).setSort(sort);

    client.findWithOptions(getCollectionName(), query, options, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        client.count(getCollectionName(), query, countRes -> {
          if (countRes.failed()) {
            handler.handle(Future.failedFuture(countRes.cause()));
          } else {
            final long totalResults = countRes.result();
            final List<T> results = res.result().stream().map(obj -> obj.mapTo(modelClass)).collect(Collectors.toList());

            handler.handle(Future.succeededFuture(new PaginatedResult<>(results, paginatedOption.getPerPage(), totalResults)));
          }
        });
      }
    });
  }

  public void find(final String id, final Handler<AsyncResult<T>> handler) {
    final JsonObject query = new JsonObject().put("_id", id);
    client.findOne(getCollectionName(), query, null, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        final JsonObject result = res.result();

        if (result == null) {
          handler.handle(Future.failedFuture(new NoSuchElementException("There is no document with ID " + id)));
        } else {
          handler.handle(Future.succeededFuture(result.mapTo(modelClass)));
        }
      }
    });
  }

  public static MongoClient createClient(final Vertx vertx) {
    final JsonObject mongoConfig = ConfigurationService.getConf().getJsonObject("mongo", new JsonObject());
    final JsonObject config = new JsonObject().put("db_name", mongoConfig.getString("db")).put("connection_string",
        mongoConfig.getString("connection_string", "mongodb://localhost:27017"));

    return MongoClient.createShared(vertx, config);
  }

}
