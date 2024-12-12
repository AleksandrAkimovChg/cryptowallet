package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAccountDtoReq {
    @NonNull
    private final String username;
    @JsonProperty("crypto_type")
    private final String cryptoType;
}
