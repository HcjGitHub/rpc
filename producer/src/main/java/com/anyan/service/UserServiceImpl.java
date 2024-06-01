package com.anyan.service;

import com.anyan.entity.User;
import com.anyan.service.UserService;

/**
 * @author anyan
 * DateTime: 2024/5/31
 */

public class UserServiceImpl implements UserService {
    @Override
    public String getUserName(User user) {
        return user.getName();
    }
}
