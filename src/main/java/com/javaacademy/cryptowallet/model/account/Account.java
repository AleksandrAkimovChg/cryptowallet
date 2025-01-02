package com.javaacademy.cryptowallet.model.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String login;
    private CryptoCoinType coin;
    private BigDecimal balance;
    private UUID uuid;

    public Account(String login, CryptoCoinType coin) {
        this.login = login;
        this.coin = coin;
        this.balance = BigDecimal.ZERO;
    }
}
