package github.yhs0092.demo.vertx.websocket.server;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class ServerMain {
  private static ExecutorService executor = Executors.newSingleThreadExecutor();

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    HttpServerOptions httpServerOptions = new HttpServerOptions()
        .setPort(18080);
    HttpServer httpServer = vertx.createHttpServer(httpServerOptions);

    Router router = Router.router(vertx);
    router.route("/ui/*").handler(StaticHandler.create().setCachingEnabled(false));
    router.route("/ws2/*").handler(rc -> {
      ServerWebSocket webSocket = rc.request().upgrade();
    });

    httpServer.websocketHandler(websocket -> {
      System.out.println("establish websocket! " + websocket.uri());
      websocket.handler(buffer -> {
        System.out.println(buffer);
        executor.execute(() -> {
          for (int i = 0; i < 3; ++i) {
            System.out.println("response! " + i);
            websocket.writeFinalTextFrame(("response! " + i + " " + new Date()));
          }
        });
      });
      websocket.closeHandler(v -> System.out.println("websocket closed! " + websocket));
    });

    httpServer.requestHandler(router).listen();
  }
}
