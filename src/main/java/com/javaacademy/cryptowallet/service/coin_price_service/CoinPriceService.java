package com.javaacademy.cryptowallet.service.coin_price_service;

import com.javaacademy.cryptowallet.model.account.CryptoCoin;

import java.math.BigDecimal;
import java.util.Optional;

public interface CoinPriceService {
    Optional<BigDecimal> getCoinPriceInUsd(CryptoCoin coin);
}
