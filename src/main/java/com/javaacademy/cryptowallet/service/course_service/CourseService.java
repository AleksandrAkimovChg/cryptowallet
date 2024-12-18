package com.javaacademy.cryptowallet.service.course_service;

import java.math.BigDecimal;

public interface CourseService {

    BigDecimal convertUsdToRub(BigDecimal amountUsd);

    BigDecimal convertRubToUsd(BigDecimal amountRub);

    BigDecimal getCourseRubToUsd();
}
