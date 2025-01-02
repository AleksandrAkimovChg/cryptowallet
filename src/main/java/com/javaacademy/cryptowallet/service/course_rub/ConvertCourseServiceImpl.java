package com.javaacademy.cryptowallet.service.course_rub;

import com.javaacademy.cryptowallet.http_client.OkClient;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
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
public class ConvertCourseServiceImpl implements ConvertCourseService {
    private final OkClient client;
    @Value("${app.course.api}")
    private String urlPath;
    private static final String JSON_PATH_TEMPLATE_FOR_USD = "$.rates.USD";
    private static final int SCALE_FOR_DIVIDE = 13;
    private static final int SCALE_FOR_MULTIPLY = 10;

    @Override
    public Optional<BigDecimal> convertUsdToRub(BigDecimal amountUsd) {
        BigDecimal response = getCourseRubToUsd().orElse(null);
        return response != null ? Optional.of(
                amountUsd.divide(response, SCALE_FOR_DIVIDE, RoundingMode.HALF_UP)) : Optional.empty();
    }

    @Override
    public Optional<BigDecimal> convertRubToUsd(BigDecimal amountRub) {
        BigDecimal response = getCourseRubToUsd().orElse(null);
        return response != null ? Optional.of(
                amountRub.multiply(response).setScale(SCALE_FOR_MULTIPLY, RoundingMode.HALF_UP)) : Optional.empty();
    }

    private Optional<BigDecimal> getCourseRubToUsd() {
        String responseBody = getCourseForRub().orElse(null);
        log.info("получение курса доллара на 1 рубль - парсинг строки {}", responseBody);
        return Optional.of(JsonPath.parse(responseBody)
                .read(JsonPath.compile(JSON_PATH_TEMPLATE_FOR_USD), BigDecimal.class));
    }

    private Optional<String> getCourseForRub() {
        log.info("запрос курса рубля к рубля к другим валютам");
        Request request = client.getGetRequest(urlPath);
        try (Response response = client.sendRequest(request)) {
            return Optional.of(client.getResponseBody(response));
        } catch (IOException e) {
            log.info("Ошибка при выполнении запроса к внешнему сервису");
            log.warn(e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
