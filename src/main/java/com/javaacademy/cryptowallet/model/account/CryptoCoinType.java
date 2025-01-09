package com.javaacademy.cryptowallet.model.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CryptoCoinType {
    BTC("bitcoin", 8),
    ETH("ethereum", 18),
    SOL("solana", 9);

    private final String name;
    private final Integer decimalScale;
}
