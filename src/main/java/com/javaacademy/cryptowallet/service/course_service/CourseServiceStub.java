package com.javaacademy.cryptowallet.service.course_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Profile("local")
public class CourseServiceStub implements CourseService {
    private static final int SCALE_FOR_DIVIDE = 5;
    private static final int SCALE_FOR_MULTIPLY = 2;
    @Value("${app.stub.course-usd-rub}")
    private BigDecimal rateOneDollarToRouble;

    @Override
    public BigDecimal convertUsdToRub(BigDecimal amountUsd) {
        return amountUsd.multiply(rateOneDollarToRouble).setScale(SCALE_FOR_MULTIPLY, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal convertRubToUsd(BigDecimal amountRub) {
        return amountRub.divide(rateOneDollarToRouble, SCALE_FOR_DIVIDE, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getCourseRubToUsd() {
        return rateOneDollarToRouble;
    }
}
