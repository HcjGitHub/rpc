package com.anyan.rpc.config;

import com.anyan.rpc.serializer.SerializerKeys;
import lombok.Data;

import java.io.Serializable;

/**
 * 框架配置类
 *
 * @author anyan
 * DateTime: 2024/6/1
 */
@Data
public class RpcConfig implements Serializable {

    /**
     * 名称
     */
    private String name = "anyan-rpc";

    /**
     * 版本
     */
    private String version = "1.0";

    /**
     * 服务主机名
     */
    private String serverHost = "127.0.0.1";

    /**
     * 服务端口
     */
    private Integer port = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化方式
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
}
