package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Атрибуты криптовалютного кошелька в ответе на запрос о криптовалютных кошельках")
public class AccountDtoRs {
    @Schema(description = "Криптовалюта", defaultValue = "SOL")
    private final CryptoCoinType coin;
    @Schema(description = "Баланс кошелька", defaultValue = "0.12345678")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private final BigDecimal balance;
    @Schema(description = "UUID кошелька", defaultValue = "80a26662-ba14-4f87-ba75-2f9f6a13e60a")
    private final UUID uuid;
}
