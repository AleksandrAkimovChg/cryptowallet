package com.javaacademy.cryptowallet.service.integration.course_rub;

import com.javaacademy.cryptowallet.exception.CourseRubServiceNotAvailableException;
import com.jayway.jsonpath.JsonPath;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.javaacademy.cryptowallet.service.integration.coin_price.CoinPriceServiceImpl.RESPONSE_NOT_SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class CourseRubServiceImpl implements CourseRubService {
    private static final String JSON_PATH_TEMPLATE_FOR_USD = "$.rates.USD";
    @Value("${app.course.api}")
    private String urlPath;

    private final OkHttpClient okHttpClient;

    @Override
    public BigDecimal getCourseRubToUsd() {
        Request request = new Request.Builder().get().url(urlPath).build();
        log.debug("Создан request {}", request);
        try {
            @Cleanup Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful() || response.body() == null) {
                throw new CourseRubServiceNotAvailableException(RESPONSE_NOT_SUCCESS);
            }
            String responseBody = response.body().string();
            log.debug("Получен responseBody {}", responseBody);
            return JsonPath.parse(responseBody)
                    .read(JsonPath.compile(JSON_PATH_TEMPLATE_FOR_USD), BigDecimal.class);
        } catch (Exception ex) {
            throw new CourseRubServiceNotAvailableException(ex);
        }
    }
}
