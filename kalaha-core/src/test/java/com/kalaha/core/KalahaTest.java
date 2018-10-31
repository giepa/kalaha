package com.kalaha.core;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(VertxUnitRunner.class)
public class KalahaTest {
    public static final String PLAYER1 = "player1";
    public static final String PLAYER2 = "player2";

    @Test
    public void createGame(TestContext context) {
        Async async = context.async();
        Vertx vertx = Vertx.vertx();
        KalahaAPI.getInstance(vertx)
                .createGame(PLAYER1)
                .delay(50, TimeUnit.MILLISECONDS)
                .subscribe(
                        r -> KalahaAPI.getInstance(vertx)
                                .getGame(r.getId())
                                .map(res -> context.assertEquals(PLAYER1, res.getPlayer1()))
                                .subscribe(
                                        c -> async.complete(),
                                        context::fail
                                ),
                        context::fail
                );
    }

    @Test
    public void joinGame(TestContext context) {
        Async async = context.async();
        Vertx vertx = Vertx.vertx();
        KalahaAPI.getInstance(vertx)
                .createGame(PLAYER1)
                .delay(50, TimeUnit.MILLISECONDS)
                .subscribe(
                        r -> KalahaAPI.getInstance(vertx)
                                .joinGame(r.getId(), PLAYER2)
                                .map(res -> context.assertEquals(PLAYER2, res.getPlayer2()))
                                .subscribe(
                                        c -> async.complete(),
                                        context::fail
                                ),
                        context::fail
                );
    }

    @Test
    public void move(TestContext context) {
        Async async = context.async();
        Vertx vertx = Vertx.vertx();
        KalahaAPI.getInstance(vertx)
                .createGame(PLAYER1)
                .delay(50, TimeUnit.MILLISECONDS)
                .subscribe(
                        r -> KalahaAPI.getInstance(vertx)
                                .joinGame(r.getId(), PLAYER2)
                                .delay(50, TimeUnit.MILLISECONDS)
                                .subscribe(
                                        r2 -> KalahaAPI.getInstance(vertx)
                                                .move(r.getId(), PLAYER1, 0)
                                                .map(res -> context.assertEquals(0, res.getData().get(0)))
                                                .subscribe(
                                                        c -> async.complete(),
                                                        context::fail
                                                )),
                        context::fail
                );
    }
}