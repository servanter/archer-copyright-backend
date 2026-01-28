package com.archer.admin.web.sku.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import lombok.Getter;
import java.util.stream.Collectors;

@Getter
public enum StatusEnum {

    EMPTY(0, "全部"),
    PUBLISH(1, "已上架"),
DOWN(-1, "已下架"),
SALE_OUT(2, "已售罄")    ;

    private int value;
    private String label;
    public static List<Map<String, Object>> TOTALS = totals();
    private StatusEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static StatusEnum of(int value) {
        return Arrays.stream(StatusEnum.values())
                .filter(e -> e.getValue() == value)
                .findAny()
                .orElse(StatusEnum.EMPTY);
    }
    private static List<Map<String, Object>> totals() {
        return Arrays.stream(StatusEnum.values())
                .map(StatusEnum::totals0)
                .collect(Collectors.toList());
    }
    private static Map<String, Object> totals0(StatusEnum e) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", e.getValue());
        map.put("label", String.valueOf(e.getLabel()));
        return map;
    }
}