package com.anyan.rpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 *
 * @author anyan
 * DateTime: 2024/6/1
 */

public class ConfigUtils {

    /**
     * 加载配置对象，默认环境
     *
     * @param clazz       配置类
     * @param prefix      前缀
     * @param <T>
     * @return
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix) {
        return loadConfig(clazz, prefix, null);
    }

    /**
     * 加载配置对象，区分环境
     *
     * @param clazz       配置类
     * @param prefix      前缀
     * @param environment 环境
     * @return
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, String environment) {
        StringBuilder application = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            application.append("-").append(environment);
        }
        application.append(".properties");
        Props props = new Props(application.toString());
        return props.toBean(clazz, prefix);
    }
}
