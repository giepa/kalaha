package com.kalaha.db;

import com.kalaha.core.Game;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStore {
    private static final Logger LOG = LoggerFactory.getLogger(DataStore.class);

    public static String GAMES = "GAMES";

    private final Vertx vertx;

    public DataStore(Vertx vertx) {
        this.vertx = vertx;
    }

    public static DataStore getInstance(Vertx vertx) {
        return new DataStore(vertx);
    }

    public rx.Single<Game> saveGame(String id, Game game){
        return saveItem(GAMES, id, JsonObject.mapFrom(game))
                .map(r -> game);
    }

    public rx.Single<Game> getGame(String id){
        return getItem(GAMES, id)
                .map(r -> r.mapTo(Game.class));
    }

    public rx.Single<Void> saveItem(String namespace, String key, JsonObject data){
        if(vertx.isClustered()) {
            return vertx.sharedData()
                    .rxGetClusterWideMap(namespace)
                    .flatMap(map -> map.rxPut(key, data.encode()));
        } else {
            return rx.Single.<Void>just(null)
                    .doOnSuccess(r -> vertx.sharedData()
                            .getLocalMap(namespace)
                            .put(key, data.encode())
                    );
        }
    }

    public rx.Single<JsonObject> getItem(String namespace, String key){
        if(vertx.isClustered()) {
            return vertx.sharedData()
                    .rxGetClusterWideMap(namespace)
                    .flatMap(map -> map.rxGet(key))
                    .doOnSuccess(o -> {
                        if(o == null) throw new IllegalArgumentException("This item does not exist");
                    })
                    .map(Object::toString)
                    .map(Buffer::buffer)
                    .map(Buffer::toJsonObject);
        } else {
            return rx.Single.just(null)
                    .map(r -> vertx.sharedData()
                            .getLocalMap(namespace)
                            .get(key)
                    )
                    .doOnSuccess(o -> {
                        if(o == null) throw new IllegalArgumentException("This item does not exist");
                    })
                    .map(Object::toString)
                    .map(Buffer::buffer)
                    .map(Buffer::toJsonObject);
        }
    }
}
