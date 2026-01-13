package com.archer.admin.web.user.entities;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum TypeEnum {

    EMPTY(0, "全部"),
    NORMAL(1, "普通用户"),
ADMIN(2, "管理员")    ;

    private int value;
    private String label;
    public static List<Map<String, Object>> TOTALS = totals();
    private TypeEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static TypeEnum of(int value) {
        return Arrays.stream(TypeEnum.values())
                .filter(e -> e.getValue() == value)
                .findAny()
                .orElse(TypeEnum.EMPTY);
    }
    private static List<Map<String, Object>> totals() {
        return Arrays.stream(TypeEnum.values())
                .map(TypeEnum::totals0)
                .collect(Collectors.toList());
    }
    private static Map<String, Object> totals0(TypeEnum e) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", e.getValue());
        map.put("label", String.valueOf(e.getLabel()));
        return map;
    }
}