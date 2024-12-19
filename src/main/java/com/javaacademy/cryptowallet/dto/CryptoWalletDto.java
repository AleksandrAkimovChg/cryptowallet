package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoWalletDto {
    @NonNull
    @JsonProperty("account_id")
    private UUID uuid;
    @NonNull
    @JsonProperty("rubles_amount")
    private BigDecimal amountRub;
}
