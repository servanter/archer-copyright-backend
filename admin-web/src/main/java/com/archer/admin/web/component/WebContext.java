package com.archer.admin.web.component;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WebContext {

    private int userId;

    private String userName;

}
