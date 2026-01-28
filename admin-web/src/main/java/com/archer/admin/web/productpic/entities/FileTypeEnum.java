package com.archer.admin.web.productpic.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import lombok.Getter;
import java.util.stream.Collectors;

@Getter
public enum FileTypeEnum {

    EMPTY(0, "全部"),
    PIC_MAIN(1, "商品主图"),
PIC_INTRO(2, "商品介绍")    ;

    private int value;
    private String label;
    public static List<Map<String, Object>> TOTALS = totals();
    private FileTypeEnum(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static FileTypeEnum of(int value) {
        return Arrays.stream(FileTypeEnum.values())
                .filter(e -> e.getValue() == value)
                .findAny()
                .orElse(FileTypeEnum.EMPTY);
    }
    private static List<Map<String, Object>> totals() {
        return Arrays.stream(FileTypeEnum.values())
                .map(FileTypeEnum::totals0)
                .collect(Collectors.toList());
    }
    private static Map<String, Object> totals0(FileTypeEnum e) {
        Map<String, Object> map = new HashMap<>();
        map.put("value", e.getValue());
        map.put("label", String.valueOf(e.getLabel()));
        return map;
    }
}