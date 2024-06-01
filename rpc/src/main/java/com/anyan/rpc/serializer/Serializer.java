package com.anyan.rpc.serializer;

import java.io.IOException;

/**
 * 序列化器
 *
 * @author anyan
 * DateTime: 2024/6/1
 */

public interface Serializer {

    /**
     * 序列化
     *
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     *
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;
}
