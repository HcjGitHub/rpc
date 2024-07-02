package com.anyan.rpc.registry;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.anyan.rpc.config.RegistryConfig;
import com.anyan.rpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    /**
     * 本地注册节点的key集合
     */
    private final Set<String> localRegistryKeySet = new HashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        heartbeat();
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

        //将本地注册节点的key集合添加到集合中
        localRegistryKeySet.add(registryKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        //注销服务
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        kvClient.delete(key);
        log.info("scannner注销服务成功");

        //将本地注册节点的key集合移除
        localRegistryKeySet.remove(registryKey);
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

    @Override
    public void heartbeat() {
        // 10秒续约一次
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            // 续约租约
            for (String registryKey : localRegistryKeySet) {
                try {
                    // 获取键值对
                    List<KeyValue> keyValues = kvClient.get(ByteSequence.from(registryKey, StandardCharsets.UTF_8))
                            .get()
                            .getKvs();

                    //判断节点是否过期
                    if (keyValues.isEmpty()) {
                        continue;
                    }
                    // 节点未过期，续约租约（相当于重新注册）
                    KeyValue keyValue = keyValues.get(0);
                    String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                    register(serviceMetaInfo);

                } catch (Exception e) {
                    log.error("续约租约失败", e);
                }
            }
        });

        //支持秒级心跳
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
}
