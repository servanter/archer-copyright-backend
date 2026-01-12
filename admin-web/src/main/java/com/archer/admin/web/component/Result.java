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

    public static Result errorMsg(String msg) {
        return new Result(-99999, msg, "");
    }

    public static Result paramError() {
        return new Result(-10001, "参数错误", "");
    }

}
