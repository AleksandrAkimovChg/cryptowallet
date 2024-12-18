package com.javaacademy.cryptowallet.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CryptoCoin {
    BTC("bitcoin"),
    ETH("ethereum"),
    SOL("solana");

    private final String name;
}
