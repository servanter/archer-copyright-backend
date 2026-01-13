package com.archer.admin.web.login.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

public class LoginTransform {

    @Data
    public static class LoginReq{

        private String userName;
        private String password;
    }
    @Builder
    @Getter
    @JsonInclude(Include.NON_NULL)
    public static class LoginRes {

        private String token;
        private List<LoginMenuRes> menus;
    }

    @Builder
    @Getter
    @JsonInclude(Include.NON_NULL)
    public static class LoginMenuRes {

        private String path;
        private String name;
        private String label;
        private String icon;
        private String url;

        @JsonInclude(Include.NON_EMPTY)
        private List<LoginMenuRes> children;
    }

}
