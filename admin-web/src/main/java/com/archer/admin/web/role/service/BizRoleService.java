package com.archer.admin.web.role.service;

import com.archer.admin.base.entities.Role;
import com.archer.admin.base.entities.UserRole;
import com.archer.admin.base.service.RoleService;
import com.archer.admin.base.service.UserRoleService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.role.entities.RoleTransform.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BizRoleService {

    @Resource
    private RoleService roleService;
    @Resource
    private UserRoleService userRoleService;

    public RoleRes query(int roleId) {
        return RoleRes.parseRes(roleService.getById(roleId));
    }

    public Result modify(Role role) {
        return roleService.updateById(role) ? Result.success() : Result.error();
    }

    public RoleQueryRes list(RoleQueryReq roleQueryReq) {
        Page<Role> page = roleService.lambdaQuery()

                .like(StringUtils.isNotBlank(roleQueryReq.getName()), Role::getName, roleQueryReq.getName())
                .eq(roleQueryReq.getOperatorId() != 0, Role::getOperatorId, roleQueryReq.getOperatorId())
                .eq(Role::getValid, 1)

                .page(new Page<>(roleQueryReq.getPageNo(), roleQueryReq.getPageSize()));

        List<RoleRes> list = page.getRecords().stream().map(RoleRes::parseRes).collect(Collectors.toList());
        return RoleQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
    }

    public Result remove(int roleId) {
        Role role = new Role();
        role.setId(roleId);
        role.setValid(-1);
        return roleService.updateById(role) ? Result.success() : Result.error();
    }

    public Result save(Role role) {
        return roleService.save(role) ? Result.success() : Result.error();
    }

    public RoleUserRes queryUserRoles(int userId) {

        // 当前
        List<Role> roles = roleService.queryUserRoles(userId);
        List<RoleRes> curRoles = roles.stream().map(RoleRes::parseRes).collect(Collectors.toList());

        // 全部
        List<RoleRes> allRoles = roleService.lambdaQuery()
                .eq(Role::getValid, 1)
                .list()
                .stream()
                .map(RoleRes::parseRes)
                .collect(Collectors.toList());

        return RoleUserRes.builder()
                .curRoles(curRoles)
                .allRoles(allRoles)
                .build();
    }

    public Result settingRoles(UserRoleSetting userRoleSetting) {

        userRoleService.lambdaUpdate()
                .eq(UserRole::getUserId, userRoleSetting.getUserId())
                .remove();

        if (CollectionUtils.isNotEmpty(userRoleSetting.getRoleIds())) {
            List<UserRole> userRoles = userRoleSetting.getRoleIds().stream()
                    .map(roleId -> toUserRole(userRoleSetting, roleId))
                    .collect(Collectors.toList());
            userRoleService.saveBatch(userRoles);
        }

        return Result.success();
    }

    private UserRole toUserRole(UserRoleSetting userRoleSetting, Integer roleId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userRoleSetting.getUserId());
        userRole.setOperatorId(userRoleSetting.getOperatorId());
        userRole.setRoleId(roleId);
        return userRole;
    }
}
