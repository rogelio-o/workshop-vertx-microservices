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
	
	private MongoClient client;

	private CommentRepository(MongoClient client) {
		this.client = client;
	}
	
	public void insert(Comment comment, Handler<AsyncResult<Void>> handler) {
		comment.setCreationDate(LocalDateTime.now());
		comment.setUpdateDate(LocalDateTime.now());
		
		client.insert(COLLECTION, JsonObject.mapFrom(comment), res -> {
			if(res.failed()) {
				handler.handle(Future.failedFuture(res.cause()));
			} else {
				comment.setId(res.result());
				handler.handle(Future.succeededFuture());
			}
		});
	}
	
	public void update(Comment comment, Handler<AsyncResult<Void>> handler) {
		comment.setUpdateDate(LocalDateTime.now());
		
		client.save(COLLECTION, JsonObject.mapFrom(comment), res -> {
			if(res.failed()) {
				handler.handle(Future.failedFuture(res.cause()));
			} else {
				comment.setId(res.result());
				handler.handle(Future.succeededFuture());
			}
		});
	}
	
	public void delete(Comment comment, Handler<AsyncResult<Void>> handler) {
		JsonObject query = new JsonObject()
				.put("_id", comment.getId());
		client.findOneAndDelete(COLLECTION, query, HandlersUtils.fromVoidHandler(handler));
	}
	
	public void findPaginated(PaginatedOption paginatedOption, String idSerie, Handler<AsyncResult<PaginatedResult<Comment>>> handler) {
		if(paginatedOption.getPerPage() == null) {
			paginatedOption.setPerPage(DEFAULT_PER_PAGE);
		}
		
		JsonObject query = new JsonObject()
			.put("id_serie", idSerie);
		JsonObject sort = new JsonObject()
			.put("creation_date", -1);
		FindOptions options = new FindOptions()
				.setSkip(paginatedOption.getSkip())
				.setSort(sort);
		
		client.findWithOptions(COLLECTION, query, options, res -> {
			if(res.failed()) {
				handler.handle(Future.failedFuture(res.cause()));
			} else {
				List<Comment> results = res.result().stream().map(obj -> obj.mapTo(Comment.class)).collect(Collectors.toList());
				
				client.count(COLLECTION, query, countRes -> {
					if(countRes.failed()) {
						handler.handle(Future.failedFuture(countRes.cause()));
					} else {
						long totalResults = countRes.result();
						
						handler.handle(Future.succeededFuture(new PaginatedResult<>(results, paginatedOption.getPerPage(), totalResults)));
					}
				});
			}
		});
	}
	
	public void find(String id, Handler<AsyncResult<Comment>> handler) {
		JsonObject query = new JsonObject()
			.put("_id", id);
		client.findOne(COLLECTION, query, null, res -> {
			if(res.failed()) {
				handler.handle(Future.failedFuture(res.cause()));
			} else {
				JsonObject result = res.result();
				
				if(result == null) {
					handler.handle(Future.failedFuture(new NoSuchElementException("There is no comment with ID " + id)));
				} else {
					handler.handle(Future.succeededFuture(result.mapTo(Comment.class)));
				}
			}
		});
	}
	
	public static void create(MongoClient client) {
		singleton = new CommentRepository(client);
	}
	
	public static CommentRepository get() {
		return singleton;
	}
	
}
