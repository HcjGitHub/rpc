package com.anyan.rpc.model;

import cn.hutool.core.util.StrUtil;
import com.anyan.rpc.constant.RpcConstant;
import lombok.Data;

/**
 * 服务注册元信息
 *
 * @author anyan
 * DateTime: 2024/7/1
 */
@Data
public class ServiceMetaInfo {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口
     */
    private Integer servicePort;

    /**
     * 服务分组（未实现）
     */
    private String serviceGroup = "default";

    /**
     * 获取服务键名
     *
     * @return
     */
    public String getServiceKey() {
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务注册节点键名
     *
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    /**
     * 获取服务地址
     */
    public String getServiceAddress() {
        if(StrUtil.contains(serviceHost, "http")){
            return String.format("%s:%s", serviceHost, servicePort);
        }
        return String.format("http://%s:%s", serviceHost, servicePort);
    }
}
