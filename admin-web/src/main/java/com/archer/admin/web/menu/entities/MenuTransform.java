package com.archer.admin.web.menu.entities;

import com.archer.admin.base.common.Page.PageReq;
import com.archer.admin.base.common.Page.PageRes;
import com.archer.admin.base.entities.Menu;
import com.archer.admin.web.common.ValidEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MenuTransform {

    @Getter
    @Builder
    @JsonInclude(Include.NON_NULL)
    public static class MenuRes {
        // ID

        private Integer id;
        // 菜单名称

        private String name;
        // 父级菜单ID

        private Integer parentId;
        // 跳转地址

        private String url;
        // 操作人

        private Integer operatorId;
        // 状态

        private Integer valid;
        // 创建时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
        // 修改时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

        @JsonInclude(Include.NON_EMPTY)
        private List<MenuRes> children;

        public static MenuRes parseRes(Menu menu) {
            return MenuRes.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .parentId(menu.getParentId())
                    .url(menu.getUrl())
                    .operatorId(menu.getOperatorId())
                    .valid(menu.getValid())
                    .createTime(menu.getCreateTime())
                    .updateTime(menu.getUpdateTime())
                    .build();

        }

        public static MenuRes parseRes(Menu menu, List<Menu> subMenus) {
            List<MenuRes> subRes = subMenus.stream().map(MenuRes::parseRes).collect(Collectors.toList());
            return MenuRes.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .parentId(menu.getParentId())
                    .url(menu.getUrl())
                    .operatorId(menu.getOperatorId())
                    .valid(menu.getValid())
                    .createTime(menu.getCreateTime())
                    .updateTime(menu.getUpdateTime())
                    .children(subRes)
                    .build();

        }

        public String getValidStr() {
            return ValidEnum.of(valid).getLabel();
        }
    }

    @Data
    public static class MenuQueryReq extends PageReq {

        private String name;

        private int parentId;

        private String url;

        private int operatorId;

        private int valid;
    }

    @SuperBuilder
    @Getter
    public static class MenuQueryRes extends PageRes {

        private List<MenuRes> list;

    }

    @Builder
    @Getter
    public static class MenuRoleRes {

        private List<MenuRes> curMenus;
        private List<MenuRes> allMenus;

    }

    @Data
    public static class RoleMenuSetting {

        private int roleId;
        private List<Integer> menuIds;
        private int operatorId;
    }

}