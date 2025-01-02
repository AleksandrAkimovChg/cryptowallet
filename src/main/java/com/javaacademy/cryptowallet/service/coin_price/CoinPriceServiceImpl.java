package com.javaacademy.cryptowallet.service.coin_price;

import com.javaacademy.cryptowallet.http_client.OkClient;
import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod")
public class CoinPriceServiceImpl implements CoinPriceService {
    private final OkClient client;
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

    @Override
    public Optional<BigDecimal> getCoinPriceInUsd(CryptoCoinType coin) {
        String responseBody = getResponseBodyAndSendRequest(coin).orElse(null);
        log.info("получение курса coin: {} - парсинг строки {}", coin, responseBody);
        String template = jsonPathTemplateForUsd.formatted(coin.getName());
        return Optional.of(JsonPath.parse(responseBody).read(JsonPath.compile(template), BigDecimal.class));
    }

    private Optional<String> getResponseBodyAndSendRequest(CryptoCoinType coin) {
        log.info("Запрос котировки в долларах по криптовалютe: {}", coin);
        String urlPath = urlPathTemplateForUsdCoinCourse.formatted(api, coin.getName());
        Request request = client.getGetRequest(urlPath, header, token);
        try (Response response = client.sendRequest(request)) {
            return Optional.of(client.getResponseBody(response));
        } catch (IOException e) {
            log.info("Ошибка при выполнении запроса к внешнему сервису");
            log.error(e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
        return Optional.empty();
    }
}
