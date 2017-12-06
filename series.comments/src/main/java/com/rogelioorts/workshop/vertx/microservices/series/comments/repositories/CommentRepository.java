package com.rogelioorts.workshop.vertx.microservices.series.comments.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.rogelioorts.workshop.vertx.microservices.series.comments.models.Comment;
import com.rogelioorts.workshop.vertx.microservices.utils.repositories.PaginatedOption;
import com.rogelioorts.workshop.vertx.microservices.utils.repositories.PaginatedResult;
import com.rogelioorts.workshop.vertx.microservices.utils.utils.HandlersUtils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;

public final class CommentRepository {

  public static final int DEFAULT_PER_PAGE = 20;

  private static final String COLLECTION = "comments";

  private static CommentRepository singleton;

  private final MongoClient client;

  private CommentRepository(final MongoClient client) {
    this.client = client;
  }

  public void insert(final Comment comment, final Handler<AsyncResult<Void>> handler) {
    comment.setCreationDate(LocalDateTime.now());
    comment.setUpdateDate(LocalDateTime.now());

    client.insert(COLLECTION, JsonObject.mapFrom(comment), res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        comment.setId(res.result());
        handler.handle(Future.succeededFuture());
      }
    });
  }

  public void update(final Comment comment, final Handler<AsyncResult<Void>> handler) {
    comment.setUpdateDate(LocalDateTime.now());

    client.save(COLLECTION, JsonObject.mapFrom(comment), res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        comment.setId(res.result());
        handler.handle(Future.succeededFuture());
      }
    });
  }

  public void delete(final Comment comment, final Handler<AsyncResult<Void>> handler) {
    final JsonObject query = new JsonObject().put("_id", comment.getId());
    client.findOneAndDelete(COLLECTION, query, HandlersUtils.fromVoidHandler(handler));
  }

  public void findPaginated(final PaginatedOption paginatedOption, final String idSerie, final Handler<AsyncResult<PaginatedResult<Comment>>> handler) {
    if (paginatedOption.getPerPage() == null) {
      paginatedOption.setPerPage(DEFAULT_PER_PAGE);
    }

    final JsonObject query = new JsonObject().put("id_serie", idSerie);
    final JsonObject sort = new JsonObject().put("creation_date", -1);
    final FindOptions options = new FindOptions().setSkip(paginatedOption.getSkip()).setSort(sort);

    client.findWithOptions(COLLECTION, query, options, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        client.count(COLLECTION, query, countRes -> {
          if (countRes.failed()) {
            handler.handle(Future.failedFuture(countRes.cause()));
          } else {
            final long totalResults = countRes.result();
            final List<Comment> results = res.result().stream().map(obj -> obj.mapTo(Comment.class)).collect(Collectors.toList());

            handler.handle(Future.succeededFuture(new PaginatedResult<>(results, paginatedOption.getPerPage(), totalResults)));
          }
        });
      }
    });
  }

  public void find(final String id, final Handler<AsyncResult<Comment>> handler) {
    final JsonObject query = new JsonObject().put("_id", id);
    client.findOne(COLLECTION, query, null, res -> {
      if (res.failed()) {
        handler.handle(Future.failedFuture(res.cause()));
      } else {
        final JsonObject result = res.result();

        if (result == null) {
          handler.handle(Future.failedFuture(new NoSuchElementException("There is no comment with ID " + id)));
        } else {
          handler.handle(Future.succeededFuture(result.mapTo(Comment.class)));
        }
      }
    });
  }

  public static void create(final MongoClient client) {
    singleton = new CommentRepository(client);
  }

  public static CommentRepository get() {
    return singleton;
  }

}
