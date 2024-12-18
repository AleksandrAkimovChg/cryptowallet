package com.javaacademy.cryptowallet.model.account;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CryptoCoin {
    BTC("bitcoin"),
    ETH("ethereum"),
    SOL("solana");

    private final String name;
}
