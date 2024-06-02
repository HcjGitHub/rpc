package com.anyan.rpc;

import com.anyan.rpc.config.RpcConfig;
import com.anyan.rpc.constant.RpcConstant;
import com.anyan.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架启动类
 * 相当于holder,存放项目全局用到的变量，使用的双检锁单例模式
 *
 * @author anyan
 * DateTime: 2024/6/1
 */

@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;


    /**
     * 获取配置对象
     *
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }

    /**
     * 初始化配置
     */
    public static void init() {
        RpcConfig newConfig;
        try {
            newConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_RPC_PREFIX);
        } catch (Exception e) {
            newConfig = new RpcConfig();
        }
        init(newConfig);
    }

    /**
     * 初始化配置 支持自定义配置
     *
     * @param newConfig
     */
    public static void init(RpcConfig newConfig) {
        rpcConfig = newConfig;
        log.info("RpcApplication init rpc:{}", newConfig.toString());
    }
}
