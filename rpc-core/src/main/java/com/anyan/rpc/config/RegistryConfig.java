package com.anyan.rpc.config;

import lombok.Data;

/**
 * RPC 框架的注册中心配置类
 *
 * @author anyan
 * DateTime: 2024/7/1
 */
@Data
public class RegistryConfig {

    /**
     * 注册中心类别
     */
    private String registry = "etcd";

    /**
     * 注册中心地址
     */
    private String address = "http://127.0.0.1:2379";

    /**
     * 注册中心用户名
     */
    private String username;

    /**
     * 注册中心密码
     */
    private String password;

    /**
     * 注册中心超时时间(单位:ms)
     */
    private Long timeout = 10000L;
}
