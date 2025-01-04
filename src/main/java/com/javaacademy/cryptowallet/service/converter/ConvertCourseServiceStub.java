package com.javaacademy.cryptowallet.service.converter;

import com.javaacademy.cryptowallet.service.integration.course_rub.CourseRubService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Profile("local")
public class ConvertCourseServiceStub implements ConvertCourseService {
    private static final int SCALE_FOR_DIVIDE = 5;
    private static final int SCALE_FOR_MULTIPLY = 2;
    private final CourseRubService courseRubService;

    @Override
    public BigDecimal convertUsdToRub(BigDecimal amountUsd) {
        return amountUsd.multiply(courseRubService.getCourseRubToUsd())
                .setScale(SCALE_FOR_MULTIPLY, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal convertRubToUsd(BigDecimal amountRub) {
        return amountRub.divide(courseRubService.getCourseRubToUsd(), SCALE_FOR_DIVIDE, RoundingMode.HALF_UP);
    }
}
