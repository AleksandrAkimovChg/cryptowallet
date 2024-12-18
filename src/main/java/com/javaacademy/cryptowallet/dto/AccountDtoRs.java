package com.javaacademy.cryptowallet.dto;

import com.javaacademy.cryptowallet.model.account.CryptoCoin;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountDtoRs {
    private final String login;
    private final CryptoCoin coin;
    private final BigDecimal balance;
    private final UUID uuid;
}
