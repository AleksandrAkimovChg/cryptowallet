package com.javaacademy.cryptowallet.service.course_rub;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Profile("local")
public class ConvertCourseServiceStub implements ConvertCourseService {
    private static final int SCALE_FOR_DIVIDE = 5;
    private static final int SCALE_FOR_MULTIPLY = 2;
    @Value("${app.stub.course.usd-rub}")
    private BigDecimal rateOneDollarToRouble;

    @Override
    public Optional<BigDecimal> convertUsdToRub(BigDecimal amountUsd) {
        return Optional.of(amountUsd.multiply(rateOneDollarToRouble)
                .setScale(SCALE_FOR_MULTIPLY, RoundingMode.HALF_UP));
    }

    @Override
    public Optional<BigDecimal> convertRubToUsd(BigDecimal amountRub) {
        return Optional.of(amountRub.divide(rateOneDollarToRouble, SCALE_FOR_DIVIDE, RoundingMode.HALF_UP));
    }
}
