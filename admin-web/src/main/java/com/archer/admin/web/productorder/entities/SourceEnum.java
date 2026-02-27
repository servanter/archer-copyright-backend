package com.archer.admin.web.productorder.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import lombok.Getter;
import java.util.stream.Collectors;

@Getter
public enum SourceEnum {

    EMPTY(0, "全部"),
    BACKEND(1, "后台")    ;

    private int value;
    private String label;
    public static List<Map<String, Object>> TOTALS = totals();
    private SourceEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static SourceEnum of(int value) {
        return Arrays.stream(SourceEnum.values())
                .filter(e -> e.getValue() == value)
                .findAny()
                .orElse(SourceEnum.EMPTY);
    }
    private static List<Map<String, Object>> totals() {
        return Arrays.stream(SourceEnum.values())
                .map(SourceEnum::totals0)
                .collect(Collectors.toList());
    }
    private static Map<String, Object> totals0(SourceEnum e) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", e.getValue());
        map.put("label", String.valueOf(e.getLabel()));
        return map;
    }
}