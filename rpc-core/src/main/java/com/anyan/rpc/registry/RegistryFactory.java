package com.anyan.rpc.registry;

import com.anyan.rpc.spi.SpiLoader;

/**
 * @author anyan
 * DateTime: 2024/7/2
 */

public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认的注册中心 etcd
     */
    public static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取注册元信息
     *
     * @param key 注册中心 SPI 实现类的 key
     * @return
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }
}
