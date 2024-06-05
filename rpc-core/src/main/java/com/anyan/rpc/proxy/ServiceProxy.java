package com.anyan.rpc.proxy;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.anyan.rpc.model.RpcRequest;
import com.anyan.rpc.model.RpcResponse;
import com.anyan.rpc.serializer.JDKSerializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
        JDKSerializer serializer = new JDKSerializer();

        // 构造请求参数
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            // 序列化请求参数
            byte[] request = serializer.serialize(rpcRequest);
            // 远程调用
            HttpResponse httpResponse = HttpRequest.post("http://localhost:8081/")
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
