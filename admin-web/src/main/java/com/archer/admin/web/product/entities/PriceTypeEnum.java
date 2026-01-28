package com.archer.admin.web.product.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import lombok.Getter;
import java.util.stream.Collectors;

@Getter
public enum PriceTypeEnum {

    EMPTY(0, "全部"),
    FIXED_PRICE(1, "按固定定价"),
SKU_PRICE(2, "按SKU定价")    ;

    private int value;
    private String label;
    public static List<Map<String, Object>> TOTALS = totals();
    private PriceTypeEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static PriceTypeEnum of(int value) {
        return Arrays.stream(PriceTypeEnum.values())
                .filter(e -> e.getValue() == value)
                .findAny()
                .orElse(PriceTypeEnum.EMPTY);
    }
    private static List<Map<String, Object>> totals() {
        return Arrays.stream(PriceTypeEnum.values())
                .map(PriceTypeEnum::totals0)
                .collect(Collectors.toList());
    }
    private static Map<String, Object> totals0(PriceTypeEnum e) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", e.getValue());
        map.put("label", String.valueOf(e.getLabel()));
        return map;
    }
}