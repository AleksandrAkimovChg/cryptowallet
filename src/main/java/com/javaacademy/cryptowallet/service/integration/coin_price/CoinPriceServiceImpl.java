package com.javaacademy.cryptowallet.service.integration.coin_price;

import com.javaacademy.cryptowallet.exception.CoinPriceServiceNotAvailableException;
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
        String urlPath = urlPathTemplateForUsdCoinCourse.formatted(api, coin.getName());
        Request request = new Request.Builder().get().url(urlPath).build();
        log.debug("Создан request {}", request);
//        Request request = client.getGetRequest(urlPath, header, token);
        try {
            @Cleanup Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful() || response.body() == null) {
                throw new CoinPriceServiceNotAvailableException(RESPONSE_NOT_SUCCESS);
            }
            String responseBody = response.body().string();
            log.debug("Получен responseBody {}", responseBody);
            String template = jsonPathTemplateForUsd.formatted(coin.getName());
            return JsonPath.parse(responseBody).read(JsonPath.compile(template), BigDecimal.class);
        } catch (Exception ex) {
           throw new CoinPriceServiceNotAvailableException(ex);
        }
    }
}
