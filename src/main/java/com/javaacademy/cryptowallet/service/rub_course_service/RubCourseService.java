package com.javaacademy.cryptowallet.service.rub_course_service;

import java.math.BigDecimal;

public interface RubCourseService {

    BigDecimal convertUsdToRub(BigDecimal amountUsd);

    BigDecimal convertRubToUsd(BigDecimal amountRub);

    BigDecimal getCourseRubToUsd();
}
