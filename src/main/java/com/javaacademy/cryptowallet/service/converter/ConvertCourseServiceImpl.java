package com.javaacademy.cryptowallet.service.converter;

import com.javaacademy.cryptowallet.service.integration.course_rub.CourseRubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class ConvertCourseServiceImpl implements ConvertCourseService {
    private static final int SCALE_FOR_DIVIDE = 13;
    private static final int SCALE_FOR_MULTIPLY = 10;
    private final CourseRubService courseRubService;

    @Override
    public BigDecimal convertUsdToRub(BigDecimal amountUsd) {
        BigDecimal response = courseRubService.getCourseRubToUsd();
        return amountUsd.divide(response, SCALE_FOR_DIVIDE, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal convertRubToUsd(BigDecimal amountRub) {
        BigDecimal response = courseRubService.getCourseRubToUsd();
        return amountRub.multiply(response).setScale(SCALE_FOR_MULTIPLY, RoundingMode.HALF_UP);
    }
}
