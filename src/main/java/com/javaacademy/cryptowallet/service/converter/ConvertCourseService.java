package com.javaacademy.cryptowallet.service.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface ConvertCourseService {
    int SCALE_FOR_CONVERT_RUB = 7;

    default BigDecimal convertUsdToRub(BigDecimal amountUsd) {
        BigDecimal result = amountUsd.divide(getRubToUsdCourse(), SCALE_FOR_CONVERT_RUB, RoundingMode.HALF_UP);
        return result.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : result;
    }

    default BigDecimal convertRubToUsd(BigDecimal amountRub) {
        BigDecimal result = amountRub.multiply(getRubToUsdCourse())
                .setScale(SCALE_FOR_CONVERT_RUB, RoundingMode.HALF_UP);
        return result.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : result;
    }

    BigDecimal getRubToUsdCourse();
}
