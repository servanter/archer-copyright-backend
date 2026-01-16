package com.archer.admin.web.common;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SelectOptionLabel {

    private String label;
    private Object value;

    public static SelectOptionLabel of(String label, Object value) {
        return SelectOptionLabel.builder()
                .label(label)
                .value(value)
                .build();
    }
}
