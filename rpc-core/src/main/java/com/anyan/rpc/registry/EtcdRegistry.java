package com.anyan.rpc.registry;

import cn.hutool.json.JSONUtil;
import com.anyan.rpc.config.RegistryConfig;
import com.anyan.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author anyan
 * DateTime: 2024/7/1
 */
@Slf4j
public class EtcdRegistry implements Registry {

    private Client client;
    private KV kvClient;

    /**
     * etcd的根路径
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 获取lease 租约 客户端
        Lease leaseClient = client.getLeaseClient();
        // 创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();
        //设置要存储的键值对
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);
        //将键值对与租约绑定
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        //注销服务
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        kvClient.delete(key);
        log.info("scannner注销服务成功");
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //获取服务列表
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        //根据前缀获取
        try {
            GetOption getOption = GetOption.builder().
                    withPrefix(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8)).build();

            //获取键值对
            List<KeyValue> keyValues = kvClient.get(ByteSequence
                    .from(searchPrefix, StandardCharsets.UTF_8), getOption).get().getKvs();

            //转换为ServiceMetaInfo列表
            return keyValues.stream().map(keyValue -> {
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {
        log.info("etcd registry 销毁：");
        //释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
