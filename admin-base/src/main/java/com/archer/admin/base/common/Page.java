package com.archer.admin.base.common;


import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

public class Page {

    @Getter
    @SuperBuilder
    public static class PageRes {

        private long total;
        private long totalPage;
    }

    @Data
    public static class PageReq {

        private int pageNo = 1;
        private int pageSize = 20;
    }
}
