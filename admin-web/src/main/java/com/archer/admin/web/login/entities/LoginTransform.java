package com.archer.admin.web.login.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class LoginTransform {
    
    @Data
    public static class LoginReq {
        private String username;
        private String password;
    }
    
    @Getter
    @Builder
    public static class LoginRes {
        private String token;
        private List<MenuData> menuData;
    }

    @Builder
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MenuData {
        private String path;
        private String name;
        private String label;
        private String icon;
        private String url;

        @JsonInclude(JsonInclude.Include.NON_EMPTY)  
        private java.util.List<MenuData> children;
    }
}
