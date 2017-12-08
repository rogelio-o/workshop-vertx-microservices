package com.rogelioorts.workshop.vertx.microservices.scafolder.repositories;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.rogelioorts.workshop.vertx.microservices.scafolder.services.ConfigurationService;

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
    final FindOptions options = new FindOptions().setSkip(paginatedOptions.getSkip()).setSort(paginatedOptions.getSort())
        .setLimit(paginatedOptions.getPerPage());
    final JsonObject query = paginatedOptions.getQuery();

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

            final PaginatedResult<T> paginatedResult = new PaginatedResult<>();
            paginatedResult.setResults(results);
            paginatedResult.setPage(paginatedOptions.getPage());
            paginatedResult.setPerPage(paginatedOptions.getPerPage());
            paginatedResult.setTotalResults(totalResults);

            handler.handle(Future.succeededFuture(paginatedResult));
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
