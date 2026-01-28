package com.archer.admin.web.productchannel.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import java.util.stream.Collectors;

@Getter
public enum StockStrategyEnum {

    EMPTY(0, "全部"),
    SHARED(1, "共享库存"),
INDEPENDENT(2, "独立库存")    ;

    @JsonValue
    private int value;
    private String label;
    public static List<Map<String, Object>> TOTALS = totals();
    private StockStrategyEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static StockStrategyEnum of(int value) {
        return Arrays.stream(StockStrategyEnum.values())
                .filter(e -> e.getValue() == value)
                .findAny()
                .orElse(StockStrategyEnum.EMPTY);
    }
    private static List<Map<String, Object>> totals() {
        return Arrays.stream(StockStrategyEnum.values())
                .map(StockStrategyEnum::totals0)
                .collect(Collectors.toList());
    }
    private static Map<String, Object> totals0(StockStrategyEnum e) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", e.getValue());
        map.put("label", String.valueOf(e.getLabel()));
        return map;
    }
}