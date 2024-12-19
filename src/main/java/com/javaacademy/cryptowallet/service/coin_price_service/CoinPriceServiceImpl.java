package com.javaacademy.cryptowallet.service.coin_price_service;

import com.javaacademy.cryptowallet.http_client.OkClient;
import com.javaacademy.cryptowallet.model.account.CryptoCoin;
import com.javaacademy.cryptowallet.parser.CryptoParser;
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
    private final CryptoParser cryptoParser;

    @Value("${app.crypto.api}")
    private String api;
    @Value("${app.crypto.header}")
    private String header;
    @Value("${app.crypto.token}")
    private String token;
    private static final String URL_PATH_TEMPLATE_FOR_USD_COIN_COURSE = "%s/simple/price?ids=%s&vs_currencies=usd";
    private static final String JSON_PATH_TEMPLATE_FOR_USD = "$.%s.usd";

    @Override
    public Optional<BigDecimal> getCoinPriceInUsd(CryptoCoin coin) {
        String responseBody = getResponseBodyAndSendRequest(coin).orElse(null);
        log.info("получение курса coin: {} - парсинг строки {}", coin, responseBody);
        String template = JSON_PATH_TEMPLATE_FOR_USD.formatted(coin.getName());
        return responseBody != null ? cryptoParser.parseValueByTemplate(responseBody, template) : Optional.empty();
    }

    private Optional<String> getResponseBodyAndSendRequest(CryptoCoin coin) {
        log.info("Запрос котировки в долларах по криптовалютe: {}", coin);
        String urlPath = URL_PATH_TEMPLATE_FOR_USD_COIN_COURSE.formatted(api, coin.getName());
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
