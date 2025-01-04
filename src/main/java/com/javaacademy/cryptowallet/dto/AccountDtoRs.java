package com.javaacademy.cryptowallet.dto;

import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountDtoRs {
    @Schema(description = "Криптовалюта", defaultValue = "SOL")
    private final CryptoCoinType coin;
    @Schema(description = "<Баланс кошелька>", defaultValue = "0.12345678")
    private final BigDecimal balance;
    @Schema(description = "UUID кошелька", defaultValue = "80a26662-ba14-4f87-ba75-2f9f6a13e60a")
    private final UUID uuid;
}
