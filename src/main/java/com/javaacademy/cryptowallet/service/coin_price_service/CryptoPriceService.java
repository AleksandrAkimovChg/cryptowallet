package com.javaacademy.cryptowallet.service.coin_price_service;

import com.javaacademy.cryptowallet.model.account.CryptoCoin;

import java.math.BigDecimal;

public interface CryptoPriceService {
    BigDecimal getCoinPriceInUsd(CryptoCoin coin);
}
