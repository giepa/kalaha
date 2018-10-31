package com.kalaha;

import com.kalaha.web.KalahaRestAPI;
import com.kalaha.web.KalahaSockAPI;
import io.vertx.rxjava.ext.web.sstore.ClusteredSessionStore;
import io.vertx.rxjava.ext.web.sstore.LocalSessionStore;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.handler.CookieHandler;
import io.vertx.rxjava.ext.web.handler.SessionHandler;
import io.vertx.rxjava.ext.web.handler.StaticHandler;

public class Main extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        // Session Handeling
        router.route().handler(CookieHandler.create());
        if(vertx.isClustered()) {
            router.route().handler(SessionHandler
                    .create(ClusteredSessionStore.create(vertx))
            );
        } else {
            router.route().handler(SessionHandler
                    .create(LocalSessionStore.create(vertx))
            );
        }

        //UI
        StaticHandler ui = StaticHandler.create("kalaha-ui");
        router.route("/*").handler(ui);

        //SockJs Endpoint
        router.route("/poll/*").handler(KalahaSockAPI.getInstance(vertx).sockJSHandler());

        //Rest Endpoints
        router.post("/create").handler(KalahaRestAPI::create);
        router.put("/join/:id").handler(KalahaRestAPI::join);
        router.put("/move/:pit").handler(KalahaRestAPI::move);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
    }

    public static void main(String[] args){
        Vertx.vertx().rxDeployVerticle(Main.class.getName())
                .toBlocking()
                .value();
    }
}
