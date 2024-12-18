package com.javaacademy.cryptowallet.service.rub_course_service;

import com.javaacademy.cryptowallet.http_client.OkClient;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class RubCourseServiceImpl implements RubCourseService {
    private final OkClient client;
    @Value("${app.course.api:https://www.cbr-xml-daily.ru/latest.js}")
    private String urlPath;
    private static final String JSON_PATH_TEMPLATE_FOR_USD = "$.rates.USD";
    private static final int SCALE_FOR_DIVIDE = 5;
    private static final int SCALE_FOR_MULTIPLY = 2;

    @Override
    public BigDecimal convertUsdToRub(BigDecimal amountUsd) {
        BigDecimal response = getCourseRubToUsd();
        return amountUsd.divide(response, SCALE_FOR_DIVIDE, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal convertRubToUsd(BigDecimal amountRub) {
        BigDecimal response = getCourseRubToUsd();
        return amountRub.multiply(response).setScale(SCALE_FOR_MULTIPLY, RoundingMode.HALF_UP);
    }

    public BigDecimal getCourseRubToUsd() {
        String responseBody = getCourseForRub().orElseThrow();
        return JsonPath.parse(responseBody)
                .read(JsonPath.compile(JSON_PATH_TEMPLATE_FOR_USD), BigDecimal.class);
    }

    private Optional<String> getCourseForRub() {
        log.info("запрос курса рубля к рубля к другим валютам: {}");
        Request request = client.getGetRequest(urlPath);
        ResponseBody responseBody = client.sendRequest(request).body();
        try {
            if (responseBody != null) {
                return Optional.of(responseBody.string());
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
