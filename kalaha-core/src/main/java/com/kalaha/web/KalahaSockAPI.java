package com.kalaha.web;

import com.kalaha.core.Game;
import com.kalaha.core.KalahaAPI;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.eventbus.MessageConsumer;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.rxjava.ext.web.handler.sockjs.SockJSSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kalaha.core.KalahaAPI.GAMES;

public class KalahaSockAPI {
    private static final Logger LOG = LoggerFactory.getLogger(KalahaSockAPI.class);

    private final Vertx vertx;

    public KalahaSockAPI(Vertx vertx) {
        this.vertx = vertx;
    }

    public static KalahaSockAPI getInstance(Vertx vertx) {
        return new KalahaSockAPI(vertx);
    }

    public SockJSHandler sockJSHandler() {
        SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);
        return SockJSHandler
                .create(vertx, options)
                .socketHandler(this::initSession);
    }

    private void initSession(SockJSSocket socket){
        String id = socket.webSession().get(GAMES);
        if(id == null) {
            socket.close();
            return;
        }
        String sessionId = socket.webSession().id();
        MessageConsumer<String> consumer = vertx.eventBus().consumer(id);
        socket.endHandler(s -> consumer.unregister());
        KalahaAPI.getInstance(vertx).getGame(id)
                .map(JsonObject::mapFrom)
                .map(r -> r.put("playerId", sessionId))
                .map(JsonObject::encode)
                .doOnSuccess(socket::write)
                .flatMapObservable(r -> consumer.toObservable())
                .map(r -> Buffer.buffer(r.body()).toJsonObject())
                .map(r -> r.put("playerId", sessionId))
                .doOnNext(r -> socket.write(r.encode()))
                .doOnError(e -> {
                    try {
                        socket.end();
                    } catch (Exception ex) {
                    }
                })
                .doOnCompleted(() -> {
                    try {
                        socket.end();
                    } catch (Exception ex) {
                    }
                })
                .subscribe(
                        r -> {},
                        e -> LOG.error("Error reading task progress stream", e)
                );
    }

    public void fireEvent(Game game){
        vertx.eventBus().publish(game.getId(), JsonObject.mapFrom(game).encode());
    }

}
