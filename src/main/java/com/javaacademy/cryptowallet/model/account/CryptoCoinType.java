package com.javaacademy.cryptowallet.model.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CryptoCoinType {
    BTC("bitcoin"),
    ETH("ethereum"),
    SOL("solana");

    private final String name;
}
