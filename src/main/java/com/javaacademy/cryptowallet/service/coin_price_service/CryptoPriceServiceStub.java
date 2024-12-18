package com.javaacademy.cryptowallet.service.coin_price_service;

import com.javaacademy.cryptowallet.model.CryptoCoin;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Profile("local")
public class CryptoPriceServiceStub implements CryptoPriceService {
    @Value("${app.stub.course-coin}")
    private BigDecimal currencyStub;

    @Override
    public BigDecimal getCoinPriceInUsd(CryptoCoin coin) {
        return currencyStub;
    }
}
