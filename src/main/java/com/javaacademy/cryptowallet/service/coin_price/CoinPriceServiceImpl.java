package com.javaacademy.cryptowallet.service.coin_price;

import com.javaacademy.cryptowallet.exception.CoinPriceServiceNotAvailableException;
import com.javaacademy.cryptowallet.exception.CourseRubServiceNotAvailableException;
import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class CoinPriceServiceImpl implements CoinPriceService {
    public static final String RESPONSE_NOT_SUCCESS = "Ответ сервиса стоимости криптовалют неуспешен или пустой";
    @Value("${app.crypto.request.api}")
    private String api;
    @Value("${app.crypto.request.header}")
    private String header;
    @Value("${app.crypto.request.token}")
    private String token;
    @Value("${app.crypto.request.path-template}")
    private String urlPathTemplateForUsdCoinCourse;
    @Value("${app.crypto.response.json-path-template}")
    private String jsonPathTemplateForUsd;
    private final OkHttpClient okHttpClient;

    @Override
    public BigDecimal getCoinPriceInUsd(CryptoCoinType coin) {
        String urlPath = getUrlPath(coin);
        Request request = getGetRequest(urlPath);
        log.debug("Создан request {}", request);
        try {
            @Cleanup Response response = sendRequest(request);
            String responseBody = getResponseBody(response);
            log.debug("Получен responseBody {}", responseBody);
            String template = jsonPathTemplateForUsd.formatted(coin.getName());
            return getCourseByJsonPath(responseBody, template);
        } catch (Exception ex) {
           throw new CoinPriceServiceNotAvailableException(ex);
        }
    }

    private String getUrlPath(CryptoCoinType coin) {
        return urlPathTemplateForUsdCoinCourse.formatted(api, coin.getName());
    }

    private Request getGetRequest(String urlPath) {
        return new Request.Builder().addHeader(header, token).get().url(urlPath).build();
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

    private BigDecimal getCourseByJsonPath(String responseBody, String template) {
        return JsonPath.parse(responseBody)
                .read(JsonPath.compile(template), BigDecimal.class);
    }
}
