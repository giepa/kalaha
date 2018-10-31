package com.kalaha.db;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(VertxUnitRunner.class)
public class DataStoreTest {

    private static String NS ="my-namespace";
    private static String KEY ="my-key";

    @Test
    public void saveItem(TestContext context) {
        Async async = context.async();
        Vertx vertx = Vertx.vertx();
        DataStore.getInstance(vertx)
                .saveItem(NS, KEY, new JsonObject().put("test", "test"))
                .delay(50, TimeUnit.MILLISECONDS)
                .subscribe(
                        r -> DataStore.getInstance(vertx)
                                .getItem(NS, KEY)
                                .map(res -> context.assertEquals("test", res.getString("test")))
                                .subscribe(
                                        c -> async.complete(),
                                        context::fail
                                ),
                        context::fail
                );
    }

}