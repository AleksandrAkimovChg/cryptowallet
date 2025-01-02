package com.javaacademy.cryptowallet.service.coin_price;

import com.javaacademy.cryptowallet.model.account.CryptoCoinType;

import java.math.BigDecimal;
import java.util.Optional;

public interface CoinPriceService {
    Optional<BigDecimal> getCoinPriceInUsd(CryptoCoinType coin);
}
