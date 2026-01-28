package com.archer.admin.web.product.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import lombok.Getter;
import java.util.stream.Collectors;

@Getter
public enum SaleStatusEnum {

    EMPTY(0, "全部"),
    IN_STOCK(1, "现货"),
PRE_SALE(2, "预售")    ;

    private int value;
    private String label;
    public static List<Map<String, Object>> TOTALS = totals();
    private SaleStatusEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static SaleStatusEnum of(int value) {
        return Arrays.stream(SaleStatusEnum.values())
                .filter(e -> e.getValue() == value)
                .findAny()
                .orElse(SaleStatusEnum.EMPTY);
    }
    private static List<Map<String, Object>> totals() {
        return Arrays.stream(SaleStatusEnum.values())
                .map(SaleStatusEnum::totals0)
                .collect(Collectors.toList());
    }
    private static Map<String, Object> totals0(SaleStatusEnum e) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", e.getValue());
        map.put("label", String.valueOf(e.getLabel()));
        return map;
    }
}