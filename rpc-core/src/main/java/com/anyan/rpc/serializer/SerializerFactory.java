package com.anyan.rpc.serializer;

import com.anyan.rpc.spi.SpiLoader;

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
     * 默认序列化器 jdk 序列化器
     */
    public static final Serializer DEFAULT_SERIALIZER = new JDKSerializer();

    /**
     * 获取序列化器 SPI 实现类
     *
     * @param key 序列化器 SPI 实现类的 key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
