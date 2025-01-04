package com.javaacademy.cryptowallet.service.converter;

import java.math.BigDecimal;

public interface ConvertCourseService {

    BigDecimal convertUsdToRub(BigDecimal amountUsd);

    BigDecimal convertRubToUsd(BigDecimal amountRub);
}
