package com.anyan.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 *
 * @author anyan
 * DateTime: 2024/6/1
 */

public class LocalRegister {

    public static final Map<String, Class<?>> serviceMap = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName  服务名
     * @param serviceClass 服务的实现类
     */
    public static void register(String serviceName, Class<?> serviceClass) {
        serviceMap.put(serviceName, serviceClass);    // 注册服务
    }

    /**
     * 获取服务
     *
     * @param serviceName 服务名
     * @return 服务的实现类
     */
    public static Class<?> getService(String serviceName) {
        return serviceMap.get(serviceName);
    }

    /**
     * 注销服务
     *
     */
    public static void remove(String serviceName) {
        serviceMap.remove(serviceName);
    }

}
