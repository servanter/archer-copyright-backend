package com.archer.admin.web.common;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ValidEnum {

    UNKNOWN(0, "全部"),
    ACTIVE(1, "正常"),
    UN_ACTIVE(-1, "失效");
    public static List<Map<String, Object>> TOTALS = totals();
    private int value;
    private String label;

    private ValidEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static ValidEnum of(int value) {
        return Stream.of(ValidEnum.values())
                .filter(e -> e.getValue() == value)
                .findAny()
                .orElse(ValidEnum.UNKNOWN);
    }

    private static List<Map<String, Object>> totals() {
        return Arrays.stream(ValidEnum.values())
                .map(ValidEnum::totals0)
                .collect(Collectors.toList());
    }

    private static Map<String, Object> totals0(ValidEnum e) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", e.getValue());
        map.put("label", String.valueOf(e.getLabel()));
        return map;
    }
}
