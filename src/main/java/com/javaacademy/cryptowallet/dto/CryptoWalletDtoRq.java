package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoWalletDtoRq {
    @Schema(description = "UUID кошелька пользователя", defaultValue = "80a26662-ba14-4f87-ba75-2f9f6a13e60a")
    @JsonProperty(value = "account_id", required = true)
    private UUID uuid;
    @Schema(description = "Сумма в рублях", defaultValue = "1000000")
    @JsonProperty(value = "rubles_amount", required = true)
    private BigDecimal amountRub;
}
