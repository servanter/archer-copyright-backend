package com.archer.admin.web.util;

import java.util.Objects;
import java.util.UUID;
import org.apache.commons.lang3.math.NumberUtils;

public class Utils {

    public static boolean isMoreThanZero(Integer digit) {
        return Objects.nonNull(digit) && digit > 0;
    }

    public static String getRandomUuid() {
        return UUID.randomUUID().toString();
    }
}
