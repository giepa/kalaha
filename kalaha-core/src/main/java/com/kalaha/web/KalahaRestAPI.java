package com.kalaha.web;

import com.kalaha.core.KalahaAPI;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import static com.kalaha.core.KalahaAPI.GAMES;

public class KalahaRestAPI {
    private static final Logger LOG = LoggerFactory.getLogger(KalahaRestAPI.class);

    public static void create(RoutingContext ctx){
        String sessionId = ctx.session().id();
        KalahaAPI.getInstance(ctx.vertx())
                .createGame(sessionId)
                .doOnSuccess(game -> ctx.session().put(GAMES, game.getId()))
                .map(JsonObject::mapFrom)
                .map(JsonObject::encodePrettily)
                .doOnSuccess(buf -> ctx.response()
                        .putHeader(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(buf.length()))
                        .putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
                        .end(buf)
                )
                .doOnError(e -> fail(e, ctx))
                .subscribe(
                        r -> LOG.info("msg=Move made by {}", sessionId),
                        e -> LOG.error("msg=Error making move by {}", sessionId, e)
                );
    }

    public static void join(RoutingContext ctx){
        String sessionId = ctx.session().id();
        String id = ctx.pathParam("id");
        KalahaAPI.getInstance(ctx.vertx())
                .joinGame(id, sessionId)
                .doOnSuccess(game -> ctx.session().put(GAMES, game.getId()))
                .map(JsonObject::mapFrom)
                .map(JsonObject::encodePrettily)
                .doOnSuccess(buf -> ctx.response()
                        .putHeader(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(buf.length()))
                        .putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
                        .end(buf)
                )
                .doOnError(e -> fail(e, ctx))
                .subscribe(
                        r -> LOG.info("msg=Move made by {}", sessionId),
                        e -> LOG.error("msg=Error making move by {}", sessionId, e)
                );
    }

    public static void move(RoutingContext ctx){
        String gameId = ctx.session().get(GAMES);
        String sessionId = ctx.session().id();
        int pit = Optional.ofNullable(ctx.pathParam("pit"))
                .filter(StringUtils::isNumeric)
                .map(Integer::parseInt)
                .orElse(-1);
        if(pit < 0 ){
            ctx.response().setStatusMessage("Invalid pit value "+ ctx.pathParam("pit"));
            ctx.fail(500);
            return;
        }
        KalahaAPI.getInstance(ctx.vertx())
                .move(gameId, sessionId, pit)
                .map(JsonObject::mapFrom)
                .map(JsonObject::encodePrettily)
                .doOnSuccess(buf -> ctx.response()
                        .putHeader(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(buf.length()))
                        .putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
                        .end(buf)
                )
                .doOnError(e -> fail(e, ctx))
                .subscribe(
                        r -> LOG.info("msg=Move made by {}", sessionId),
                        e -> LOG.error("msg=Error making move by {}", sessionId, e)
                );
    }

    private static void fail(Throwable t, RoutingContext ctx){
        String buf = new JsonObject()
                .put("error", t.getMessage())
                .encodePrettily();
        ctx.response()
                .setStatusCode(500)
                .setStatusMessage(t.getMessage())
                .putHeader(HttpHeaders.CONTENT_LENGTH.toString(), Integer.toString(buf.length()))
                .putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
                .end(buf);
    }
}
