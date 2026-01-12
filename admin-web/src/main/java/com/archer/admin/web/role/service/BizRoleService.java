package com.archer.admin.web.role.service;

import com.archer.admin.base.entities.Role;
import com.archer.admin.base.service.RoleService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.role.entities.RoleTransform.RoleQueryReq;
import com.archer.admin.web.role.entities.RoleTransform.RoleQueryRes;
import com.archer.admin.web.role.entities.RoleTransform.RoleRes;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BizRoleService {

    @Resource
    private RoleService roleService;

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
.eq(roleQueryReq.getValid() != 0, Role::getValid, roleQueryReq.getValid())

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
}
