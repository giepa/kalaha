package com.kalaha.core;

import com.kalaha.db.DataStore;
import com.kalaha.web.KalahaSockAPI;
import io.vertx.rxjava.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class KalahaAPI {
    private static final Logger LOG = LoggerFactory.getLogger(KalahaAPI.class);

    public static String GAMES = "GAMES";

    private final Vertx vertx;

    public KalahaAPI(Vertx vertx) {
        this.vertx = vertx;
    }

    public static KalahaAPI getInstance(Vertx vertx) {
        return new KalahaAPI(vertx);
    }

    public rx.Single<Game> createGame(String ownerId){
        Game game = new Game();
        game.setId(UUID.randomUUID().toString());
        game.setPlayer1(ownerId);
        game.setPlayer2(null);
        return DataStore.getInstance(vertx)
                .saveGame(game.getId(), game);
    }

    public rx.Single<Game> getGame(String id){
        return DataStore.getInstance(vertx).getGame(id);
    }

    public rx.Single<Game> joinGame(String id, String playerId){
        return getGame(id).flatMap(game ->
            Optional.ofNullable(game.getPlayer2())
                    .filter(v -> v.equals(playerId))
                    .map(v -> rx.Single.<Game>error(new IllegalStateException("This game is already in progress")))
                    .orElseGet(() -> {
                        game.setPlayer2(playerId);
                        return DataStore.getInstance(vertx)
                                .saveGame(id, game)
                                .doOnSuccess(g -> KalahaSockAPI.getInstance(vertx).fireEvent(g));
                    })
        );
    }

    public rx.Single<Game> move(String gameId, String playerId, int pit){
        return DataStore.getInstance(vertx)
                .getGame(gameId)
                .flatMap(game -> GameEngine
                        .getInstance(game)
                        .executeMove(playerId, pit, 200, TimeUnit.MILLISECONDS)
                        .doOnNext(b -> KalahaSockAPI
                                .getInstance(vertx)
                                .fireEvent(game)
                        )
                        .count()
                        .toSingle()
                        .map(r -> game)
                ).flatMap(game -> DataStore
                        .getInstance(vertx)
                        .saveGame(gameId, game)
                );
    }
}