package com.javaacademy.cryptowallet.service.course_rub;

import java.math.BigDecimal;
import java.util.Optional;

public interface ConvertCourseService {

    Optional<BigDecimal> convertUsdToRub(BigDecimal amountUsd);

    Optional<BigDecimal> convertRubToUsd(BigDecimal amountRub);
}
