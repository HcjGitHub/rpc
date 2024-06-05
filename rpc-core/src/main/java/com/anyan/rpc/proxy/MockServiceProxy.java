package com.anyan.rpc.proxy;


import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 模拟调用服务代理
 *
 * @author anyan
 * DateTime: 2024/6/1
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

        // 根据方法返回类型返回不同类型的默认值
        Class<?> returnType = method.getReturnType();
        log.info("invoke method:{}, returnType:{}", method.getName(), returnType);
        return getDefaultObject(returnType);
    }

    private Object getDefaultObject(Class<?> returnType) {
        if (returnType == null) {
            return null;
        }
        // 返回类型是基本类型
        if (returnType.isPrimitive()) {
            if (returnType == boolean.class) {
                return Boolean.FALSE;
            }
            if (returnType == short.class) {
                return (short) 0;
            }
            if (returnType == int.class) {
                return 0;
            }
            if (returnType == long.class) {
                return 0L;
            }
        }
        // 引用对象类型，直接返回null
        return null;
    }
}
