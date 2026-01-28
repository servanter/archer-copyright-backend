package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.User;
import com.archer.admin.base.repository.UserMapper;
import com.archer.admin.base.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
