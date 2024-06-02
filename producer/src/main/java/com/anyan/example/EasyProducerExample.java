package com.anyan.example;


import com.anyan.rpc.RpcApplication;
import com.anyan.rpc.register.LocalRegister;
import com.anyan.rpc.server.HttpServer;
import com.anyan.rpc.server.VertxHttpServer;
import com.anyan.service.UserService;
import com.anyan.service.UserServiceImpl;

/**
 * @author anyan
 * DateTime: 2024/5/31
 */

public class EasyProducerExample {
    public static void main(String[] args) {
        // RPC服务端初始化
        RpcApplication.init();

        //注册服务
        LocalRegister.register(UserService.class.getName(), UserServiceImpl.class);
        // 提供服务的具体实现
        HttpServer server = new VertxHttpServer();
        server.doStart(RpcApplication.getRpcConfig().getPort());

    }
}
