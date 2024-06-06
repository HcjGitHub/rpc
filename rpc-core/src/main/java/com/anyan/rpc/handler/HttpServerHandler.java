package com.anyan.rpc.handler;

import com.anyan.rpc.RpcApplication;
import com.anyan.rpc.model.RpcRequest;
import com.anyan.rpc.model.RpcResponse;
import com.anyan.rpc.register.LocalRegister;
import com.anyan.rpc.serializer.JDKSerializer;
import com.anyan.rpc.serializer.Serializer;
import com.anyan.rpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author anyan
 * DateTime: 2024/6/1
 */

public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());

        // 记录日志
        System.out.println("received request: " + request.uri() + request.method());

        // 处理请求
        request.bodyHandler(body -> {
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(body.getBytes(), RpcRequest.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 构造响应对象
            RpcResponse rpcResponse = new RpcResponse();

            // 如果请求参数为空，直接返回

            if (rpcRequest == null) {
                rpcResponse.setMessage("请求参数为空");
                doResponse(request, rpcResponse, serializer);
                return;
            }
            // 请求参数不为空，通过反射调用服务端方法
            try {
                Class<?> serviceClass = LocalRegister.getService(rpcRequest.getServiceName());
                Method method = serviceClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(serviceClass.newInstance(), rpcRequest.getArgs());
                // 设置响应结果
                rpcResponse.setMessage("success");
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());

            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);

            }
            doResponse(request, rpcResponse, serializer);
        });

    }

    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse response = request.response().putHeader("Content-Type", "application/octet-stream");
        try {
            byte[] bytes = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            response.end(Buffer.buffer());
        }
    }
}
