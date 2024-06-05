package com.anyan.example;

import com.anyan.entity.User;
import com.anyan.rpc.config.RpcConfig;
import com.anyan.rpc.proxy.ServiceProxyFactory;
import com.anyan.rpc.utils.ConfigUtils;
import com.anyan.service.UserService;

/**
 * @author anyan
 * DateTime: 2024/5/31
 */

public class EasyConsumerExample {
    public static void main(String[] args) {
        // 消费服务
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User user = new User();
        user.setName("anyan");
        String userName = userService.getUserName(user);
        System.out.println(userName);
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
        System.out.println(userService.getNumber());
    }
}
