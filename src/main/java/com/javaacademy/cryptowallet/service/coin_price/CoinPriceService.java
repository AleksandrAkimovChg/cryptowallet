package com.javaacademy.cryptowallet.service.coin_price;

import com.javaacademy.cryptowallet.model.account.CryptoCoinType;

import java.math.BigDecimal;

public interface CoinPriceService {
    BigDecimal getCoinPriceInUsd(CryptoCoinType coin);
}
