package com.archer.admin.web.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zhanghongyan p_hyanzhang
 * @Date: 2023/11/13 10:55 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {

    private static final Result ERROR = new Result(-99999, "出错啦，请稍后重试", "");
    public static final Result TOKEN_EXPIRED = new Result(-99991, "请重新登录", "");

    public static final Result FILE_NOT_FOUND = new Result(-80001, "文件不能为空", "");
    private static final Result SUCCESS = new Result(0, "success", "");

    private int code;

    private String msg;

    private T data;

    public static Result success() {
        return SUCCESS;
    }

    public static <T> Result<T> success(T t) {
        return new Result<T>(0, "success", t);
    }

    public static Result error() {
        return ERROR;
    }

    public static Result fileError() {
        return FILE_NOT_FOUND;
    }

    public static Result error(int code, String msg) {
        return new Result(code, msg, "");
    }

    public static Result errorMsg(String msg) {
        return new Result(-99999, msg, "");
    }

    public static Result paramError() {
        return new Result(-10001, "参数错误", "");
    }

}
