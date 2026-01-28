package com.archer.admin.web.common;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SelectOptionLabel {

    private String label;
    private Object value;
    private List<SelectOptionLabel> children;

    public static SelectOptionLabel of(String label, Object value) {
        return SelectOptionLabel.builder()
                .label(label)
                .value(value)
                .build();
    }

    public static SelectOptionLabel of(String label, Object value, List<SelectOptionLabel> children) {
        return SelectOptionLabel.builder()
                .label(label)
                .value(value)
                .children(children)
                .build();
    }
}
