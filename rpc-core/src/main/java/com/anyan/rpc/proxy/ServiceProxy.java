package com.anyan.rpc.proxy;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.anyan.rpc.RpcApplication;
import com.anyan.rpc.config.RegistryConfig;
import com.anyan.rpc.constant.RpcConstant;
import com.anyan.rpc.model.RpcRequest;
import com.anyan.rpc.model.RpcResponse;
import com.anyan.rpc.model.ServiceMetaInfo;
import com.anyan.rpc.registry.Registry;
import com.anyan.rpc.registry.RegistryFactory;
import com.anyan.rpc.serializer.Serializer;
import com.anyan.rpc.serializer.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务代理（JDK代理）
 *
 * @author anyan
 * DateTime: 2024/6/1
 */

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

        // 序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求参数
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            // 序列化请求参数
            byte[] request = serializer.serialize(rpcRequest);
            // 从注册中心获取服务地址
            RegistryConfig registryConfig = RpcApplication.getRpcConfig().getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());

            if (CollUtil.isEmpty(serviceMetaInfos)) {
                throw new RuntimeException("服务[" + serviceName + "]未注册");
            }

            ServiceMetaInfo metaInfo = serviceMetaInfos.get(0);
            // 远程调用
            HttpResponse httpResponse = HttpRequest.post(metaInfo.getServiceAddress())
                    .body(request)
                    .execute();

            byte[] bodyBytes = httpResponse.bodyBytes();
            // 反序列化响应结果
            RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);

            return rpcResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
