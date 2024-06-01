package com.anyan.rpc.server;

/**
 * 定义统一的接口服务
 * @author anyan
 * DateTime: 2024/6/1
 */

public interface HttpServer {

    /**
     * 启动服务器
     */
    void doStart(int port);
}
