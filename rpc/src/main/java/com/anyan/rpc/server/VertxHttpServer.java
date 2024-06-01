package com.anyan.rpc.server;

import com.anyan.rpc.handler.HttpServerHandler;
import io.vertx.core.Vertx;

/**
 * VertxHttpServer 服务器
 * @author anyan
 * DateTime: 2024/6/1
 */

public class VertxHttpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        // 创建Vertx实例
        Vertx vertx = Vertx.vertx();
        // 创建HttpServer实例
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        // 设置路由
        server.requestHandler(new HttpServerHandler());
        // 绑定端口 启动服务器
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server started on port " + port);
            }else {
                System.out.println("Failed to start server on port " + port + " " + result.cause());
            }
        });


    }
}
