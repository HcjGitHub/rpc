package com.anyan.rpc.proxy;


import java.lang.reflect.Proxy;

/**
 * 服务代理工厂
 *
 * @author anyan
 * DateTime: 2024/6/1
 */

public class ServiceProxyFactory {

    /**
     * 根据服务类获取对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}
