package com.javaacademy.cryptowallet.service.coin_price_service;

import com.javaacademy.cryptowallet.model.CryptoCoin;

import java.math.BigDecimal;

public interface CryptoPriceService {
    BigDecimal getCoinPriceInUsd(CryptoCoin coin);
}
