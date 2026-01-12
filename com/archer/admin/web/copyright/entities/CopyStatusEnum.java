package com.archer.admin.web.copyright.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import lombok.Getter;
import java.util.stream.Collectors;

@Getter
public enum CopyStatusEnum {

    EMPTY(0, ""),
    VALID(1, "正常"),
EXPIRE(-1, "已到期")    ;

    private int value;
    private String label;
    public static List<Map<String, Object>> TOTALS = totals();
    private CopyStatusEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static CopyStatusEnum of(int value) {
        return Arrays.stream(CopyStatusEnum.values())
                .filter(e -> e.getValue() == value)
                .findAny()
                .orElse(CopyStatusEnum.EMPTY);
    }
    private static List<Map<String, Object>> totals() {
        return Arrays.stream(CopyStatusEnum.values())
                .map(CopyStatusEnum::totals0)
                .collect(Collectors.toList());
    }
    private static Map<String, Object> totals0(CopyStatusEnum e) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", e.getValue());
        map.put("label", String.valueOf(e.getLabel()));
        return map;
    }
}