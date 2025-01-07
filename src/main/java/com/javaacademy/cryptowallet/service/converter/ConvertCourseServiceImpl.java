package com.javaacademy.cryptowallet.service.converter;

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

import java.io.IOException;
import java.math.BigDecimal;

import static com.javaacademy.cryptowallet.service.coin_price.CoinPriceServiceImpl.RESPONSE_NOT_SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class ConvertCourseServiceImpl implements ConvertCourseService {
    private static final String JSON_PATH_TEMPLATE_FOR_USD = "$.rates.USD";
    @Value("${app.course.api}")
    private String urlPath;
    private final OkHttpClient okHttpClient;

    @Override
    public BigDecimal getRubToUsdCourse() {
        Request request = getRequest(urlPath);
        log.debug("Создан request {}", request);
        try {
            @Cleanup Response response = sendRequest(request);
            String responseBody = getResponseBody(response);
            log.debug("Получен responseBody {}", responseBody);
            return getCourseByJsonPath(responseBody);
        } catch (Exception ex) {
            throw new CourseRubServiceNotAvailableException(ex);
        }
    }

    private Request getRequest(String urlPath) {
        return new Request.Builder().get().url(urlPath).build();
    }

    private Response sendRequest(Request request) throws IOException {
        return okHttpClient.newCall(request).execute();
    }

    private String getResponseBody(Response response) throws IOException {
        if (!response.isSuccessful() || response.body() == null) {
            throw new CourseRubServiceNotAvailableException(RESPONSE_NOT_SUCCESS);
        }
        return response.body().string();
    }

    private BigDecimal getCourseByJsonPath(String responseBody) {
        return JsonPath.parse(responseBody)
                .read(JsonPath.compile(JSON_PATH_TEMPLATE_FOR_USD), BigDecimal.class);
    }
}
