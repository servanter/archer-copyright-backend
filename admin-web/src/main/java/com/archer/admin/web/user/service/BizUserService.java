package com.archer.admin.web.user.service;

import com.archer.admin.base.entities.User;
import com.archer.admin.base.service.UserService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.user.entities.UserTransform.UserQueryReq;
import com.archer.admin.web.user.entities.UserTransform.UserQueryRes;
import com.archer.admin.web.user.entities.UserTransform.UserRes;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BizUserService {

    @Resource
    private UserService userService;

    public UserRes query(int userId) {
        return UserRes.parseRes(userService.getById(userId));
    }

    public Result modify(User user) {
        return userService.updateById(user) ? Result.success() : Result.error();
    }

    public UserQueryRes list(UserQueryReq userQueryReq) {
        Page<User> page = userService.lambdaQuery()
                
.like(StringUtils.isNotBlank(userQueryReq.getUserName()), User::getUserName, userQueryReq.getUserName())
.like(StringUtils.isNotBlank(userQueryReq.getPassword()), User::getPassword, userQueryReq.getPassword())
.eq(User::getValid, 1)
.eq(userQueryReq.getType() != 0, User::getType, userQueryReq.getType())

                .page(new Page<>(userQueryReq.getPageNo(), userQueryReq.getPageSize()));

        List<UserRes> list = page.getRecords().stream().map(UserRes::parseRes).collect(Collectors.toList());
        return UserQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
    }

    public Result remove(int userId) {
        User user = new User();
        user.setId(userId);
        user.setValid(-1);
        return userService.updateById(user) ? Result.success() : Result.error();
    }

    public Result save(User user) {
        return userService.save(user) ? Result.success() : Result.error();
    }
}
