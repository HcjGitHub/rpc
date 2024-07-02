package com.anyan.example;


import com.anyan.rpc.RpcApplication;
import com.anyan.rpc.config.RegistryConfig;
import com.anyan.rpc.config.RpcConfig;
import com.anyan.rpc.model.ServiceMetaInfo;
import com.anyan.rpc.registry.LocalRegistry;
import com.anyan.rpc.registry.Registry;
import com.anyan.rpc.registry.RegistryFactory;
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
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        try {
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getPort());
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 提供服务的具体实现
        HttpServer server = new VertxHttpServer();
        server.doStart(RpcApplication.getRpcConfig().getPort());

    }
}
