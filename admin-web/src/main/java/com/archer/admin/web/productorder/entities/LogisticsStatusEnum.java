package com.archer.admin.web.productorder.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import lombok.Getter;
import java.util.stream.Collectors;

@Getter
public enum LogisticsStatusEnum {

    EMPTY(0, "全部"),
    WAIT_DELIVERY(1, "待发货"),
    DELIVERED(2, "已发货"),
    PART_DELIVERED(3, "部分发货");

    private int value;
    private String label;
    public static List<Map<String, Object>> TOTALS = totals();

    private LogisticsStatusEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static LogisticsStatusEnum of(int value) {
        return Arrays.stream(LogisticsStatusEnum.values())
                .filter(e -> e.getValue() == value)
                .findAny()
                .orElse(LogisticsStatusEnum.EMPTY);
    }

    private static List<Map<String, Object>> totals() {
        return Arrays.stream(LogisticsStatusEnum.values())
                .map(LogisticsStatusEnum::totals0)
                .collect(Collectors.toList());
    }

    private static Map<String, Object> totals0(LogisticsStatusEnum e) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", e.getValue());
        map.put("label", String.valueOf(e.getLabel()));
        return map;
    }
}