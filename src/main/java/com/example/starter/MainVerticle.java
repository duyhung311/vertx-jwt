package com.example.starter;

import com.example.starter.security.CredentialUserDetailService;
import com.example.starter.service.JwtService;
import com.sun.tools.javac.Main;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.impl.BodyHandlerImpl;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    Router router = Router.router(vertx);

    // Create a JWT Auth Provider
    JWTAuth jwt = JWTAuth.create(vertx, new JWTAuthOptions()
      .setKeyStore(new KeyStoreOptions()
//        .setType("jceks")
//        .setPath("keystore.jceks")
        .setPassword("secret")));


    JwtService jwtService = JwtService.getInstance();
    CredentialUserDetailService service = new CredentialUserDetailService();
    router.get("/api/newToken").handler(ctx -> {
      ctx.response().putHeader("Content-Type", "text/plain");
      ctx.response().end(jwt.generateToken(new JsonObject(), new JWTOptions().setExpiresInSeconds(60)));
    });


    // protect the API
    router.route("/api/*").handler(JWTAuthHandler.create(jwt));

    // this is the secret API
    router.get("/api/protected").handler(ctx -> {
      ctx.response().putHeader("Content-Type", "text/plain");
      ctx.response().end("a secret you should keep for yourself...");
    });

    // Serve the non-private static pages
    router.route().handler(StaticHandler.create("io/vertx/example/web/jwt/webroot"));

    vertx.createHttpServer().requestHandler(router).listen(8080);
  }

  public static void main(String[] args) {
    Launcher.executeCommand("run", MainVerticle.class.getName());
  }
}
