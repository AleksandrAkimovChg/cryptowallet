package com.javaacademy.cryptowallet.service.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Profile("local")
public class ConvertCourseServiceStub implements ConvertCourseService {
    @Value("${app.stub.course.usd-rub}")
    private BigDecimal rateOneDollarToRouble;

    @Override
    public BigDecimal getRubToUsdCourse() {
        return BigDecimal.ONE.divide(rateOneDollarToRouble, SCALE_FOR_CONVERT_RUB, RoundingMode.HALF_UP);
    }
}
