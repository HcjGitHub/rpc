package com.anyan.rpc.serializer;

import com.anyan.rpc.model.RpcRequest;
import com.anyan.rpc.model.RpcResponse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo 序列化器
 *
 * @author anyan
 * DateTime: 2024/6/6
 */
public class KryoSerializer implements Serializer {
    // 使用 ThreadLocal 来确保每个线程都有一个独立的 Kryo 实例，因为 Kryo 实例不是线程安全的
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 注册需要序列化的类
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        // 使用 ByteArrayOutputStream 来存储序列化后的字节数据
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             // 使用 Kryo 的 Output 类将对象写入字节数组输出流
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            // 序列化对象并写入输出流
            kryo.writeObject(output, obj);
            output.close();
            // 返回序列化后的字节数组
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException {
        // 使用 ByteArrayInputStream 来读取字节数据
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             // 使用 Kryo 的 Input 类从字节数组输入流读取数据
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            // 反序列化对象并返回
            return kryo.readObject(input, classType);
        }
    }
}

