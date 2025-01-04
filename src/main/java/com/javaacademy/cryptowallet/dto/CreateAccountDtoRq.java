package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAccountDtoRq {
    @Schema(description = "Логин пользователя", defaultValue = "test123")
    @NonNull
    private final String username;
    @Schema(description = "Тип криптовалюты", defaultValue = "SOL")
    @JsonProperty("crypto_type")
    private final String cryptoType;
}
