package com.anyan.rpc.serializer;

import spi.SpiLoader;

/**
 * 序列化工厂类 用于获取序列化器 SPI 实现类
 *
 * @author anyan
 * DateTime: 2024/6/6
 */

public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 获取序列化器 SPI 实现类
     *
     * @param key 序列化器 SPI 实现类的 key
     * @return
     */
    public static Serializer getSerializer(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
