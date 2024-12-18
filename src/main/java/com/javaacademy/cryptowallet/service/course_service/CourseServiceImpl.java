package com.javaacademy.cryptowallet.service.course_service;

import com.javaacademy.cryptowallet.http_client.OkClient;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class CourseServiceImpl implements CourseService {
    private final OkClient client;

    @Value("${app.course.api:https://www.cbr-xml-daily.ru/latest.js}")
    private String url;
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

    @SneakyThrows
    public BigDecimal getCourseRubToUsd() {
        Response response = getCourseForRub();
        String str = response.body().string();
        log.info("полученный response.body().string(): {}", str);
        return JsonPath.parse(str)
                .read(JsonPath.compile(JSON_PATH_TEMPLATE_FOR_USD), BigDecimal.class);
    }

    @SneakyThrows
    private Response getCourseForRub() {
        log.info("запрос курса рубля к рубля к другим валютам: {}");
        log.info("urlPath: {}", url);
        Request request = new Request.Builder().get().url(url).build();
        log.info("request: {}", request);
        Response response = null;
        try {
            response = client.getClient().newCall(request).execute();
            log.info("получен response: {}", response);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (!response.isSuccessful() || response.body() == null) {
            throw new RuntimeException("Response неуспешен или пустой");
        }
        System.out.println(response.toString());
        return response;
    }
}
