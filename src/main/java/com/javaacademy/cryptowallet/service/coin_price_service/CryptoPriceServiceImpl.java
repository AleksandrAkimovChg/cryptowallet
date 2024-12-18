package com.javaacademy.cryptowallet.service.coin_price_service;

import com.javaacademy.cryptowallet.http_client.OkClient;
import com.javaacademy.cryptowallet.model.CryptoCoin;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class CryptoPriceServiceImpl implements CryptoPriceService {
    private final OkClient client;

    @Value("${app.crypto.api}")
    private String api;
    @Value("${app.crypto.header}")
    private String header;
    @Value("${app.crypto.token}")
    private String token;
    private static final String PATH_TEMPLATE_FOR_USD_COIN_COURSE = "%s/simple3/price?ids=%s&vs_currencies=usd";
    private static final String JSON_PATH_TEMPLATE_FOR_USD = "$.%s.usd";

    @Override
    public BigDecimal getCoinPriceInUsd(CryptoCoin coin) {
        String responseBody = sendRequest(coin);
        return JsonPath.parse(responseBody)
                .read(JsonPath.compile(JSON_PATH_TEMPLATE_FOR_USD.formatted(coin.getName())), BigDecimal.class);
    }

    private String sendRequest(CryptoCoin coin) {
        log.info("запрос котировки в долларах по криптовалютe: {}", coin);
        String urlPath = PATH_TEMPLATE_FOR_USD_COIN_COURSE.formatted(api, coin.getName());
        Request request = client.getGetRequest(urlPath, header, token);
        String result = "";
        try {
            Response response = client.sendRequest(request);
            if (response == null || !response.isSuccessful() || response.body() == null) {
                throw new RuntimeException("Response неуспешен или пустой");
            }
            result = client.getResponseBody(response);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }
}
