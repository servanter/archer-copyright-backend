package com.archer.admin.web.product.entities;

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
    UNLISTED(1, "未上架"),
LISTED(2, "已上架"),
TAKEN_OFF(3, "已下架"),
AUTO_TAKEN_OFF(4, "自动下架"),
SOLD_OUT(5, "售罄"),
    SOLD_OUT_TAKEN_OFF(6, "已下架-售罄")    ;

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