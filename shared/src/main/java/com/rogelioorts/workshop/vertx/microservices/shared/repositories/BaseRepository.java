package com.rogelioorts.workshop.vertx.microservices.shared.repositories;

import java.util.NoSuchElementException;

import com.rogelioorts.workshop.vertx.microservices.shared.services.ConfigurationService;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public abstract class BaseRepository<T extends Model> {

  private final MongoClient client;

  private final Class<T> modelClass;

  protected BaseRepository(final MongoClient client, final Class<T> modelClass) {
    this.client = client;
    this.modelClass = modelClass;
  }

  protected abstract String getCollectionName();

  protected void beforeInsert(final T model, final Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.succeededFuture());
  }

  protected void afterInsert(final T model, final Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.succeededFuture());
  }

  public void insert(final T model, final Handler<AsyncResult<Void>> handler) {
    // #PLACEHOLDER-22a
  }

  protected void beforeUpdate(final T model, final Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.succeededFuture());
  }

  protected void afterUpdate(final T model, final Handler<AsyncResult<Void>> handler) {
    handler.handle(Future.succeededFuture());
  }

  public void update(final String id, final T model, final Handler<AsyncResult<T>> handler) {
    model.setId(id);

    beforeUpdate(model, beforeRes -> {
      if (beforeRes.failed()) {
        handler.handle(Future.failedFuture(beforeRes.cause()));
      } else {
        final JsonObject query = new JsonObject().put("_id", id);
        client.findOneAndReplace(getCollectionName(), query, JsonObject.mapFrom(model), res -> {
          if (res.failed()) {
            handler.handle(Future.failedFuture(res.cause()));
          } else {
            final JsonObject jsonModel = res.result();
            if (jsonModel == null) {
              handler.handle(Future.failedFuture(new NoSuchElementException("Not found document with ID " + id)));
            } else {
              afterUpdate(model, afterRes -> {
                if (afterRes.failed()) {
                  handler.handle(Future.failedFuture(afterRes.cause()));
                } else {
                  handler.handle(Future.succeededFuture(model));
                }
              });
            }
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

  public void delete(final String id, final Handler<AsyncResult<Void>> handler) {
    find(id, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        final T model = res.result();

        if (model == null) {
          handler.handle(Future.failedFuture(new NoSuchElementException("Not found document with ID " + id)));
        } else {
          delete(model, handler);
        }
      }
    });
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

  public void findPaginated(final PaginatedOptions paginatedOptions, final Handler<AsyncResult<PaginatedResult<T>>> handler) {
    // #PLACEHOLDER-22b
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
