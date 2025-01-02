package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.javaacademy.cryptowallet.model.account.CryptoCoinType;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAccountDtoRq {
    @NonNull
    private final String username;
    @JsonProperty("crypto_type")
    private final CryptoCoinType cryptoType;
}
