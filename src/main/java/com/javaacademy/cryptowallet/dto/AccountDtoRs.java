package com.javaacademy.cryptowallet.dto;

import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountDtoRs {
    private final String login;
    private final CryptoCoinType coin;
    private final BigDecimal balance;
    private final UUID uuid;
}
