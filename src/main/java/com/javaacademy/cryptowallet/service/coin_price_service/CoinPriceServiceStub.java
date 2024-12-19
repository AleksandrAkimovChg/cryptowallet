package com.javaacademy.cryptowallet.service.coin_price_service;

import com.javaacademy.cryptowallet.model.account.CryptoCoin;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Profile("local")
public class CoinPriceServiceStub implements CoinPriceService {
    @Value("${app.stub.course.coin-usd}")
    private BigDecimal currencyStub;

    @Override
    public Optional<BigDecimal> getCoinPriceInUsd(CryptoCoin coin) {
        return Optional.ofNullable(currencyStub);
    }
}
