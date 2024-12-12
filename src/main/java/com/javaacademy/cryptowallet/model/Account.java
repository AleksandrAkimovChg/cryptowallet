package com.javaacademy.cryptowallet.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Account {
    private final String login;
    private final CryptoCoin coin;
    private BigDecimal balance = BigDecimal.ZERO;
    private final UUID uuid = UUID.randomUUID();

    public Account(String login, CryptoCoin coin) {
        this.login = login;
        this.coin = coin;
    }
}
