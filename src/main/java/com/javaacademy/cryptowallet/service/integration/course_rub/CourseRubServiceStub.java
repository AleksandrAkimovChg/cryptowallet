package com.javaacademy.cryptowallet.service.integration.course_rub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Profile("local")
public class CourseRubServiceStub implements CourseRubService {
    @Value("${app.stub.course.usd-rub}")
    private BigDecimal rateOneDollarToRouble;

    @Override
    public BigDecimal getCourseRubToUsd() {
        return rateOneDollarToRouble;
    }
}
