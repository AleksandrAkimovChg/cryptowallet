package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Атрибуты для запроса на создание криптовалютного кошелька")
public class CreateAccountDtoRq {
    @Schema(description = "Логин пользователя", defaultValue = "test123")
    @JsonProperty(value = "username", required = true)
    private final String username;
    @Schema(description = "Тип криптовалюты", defaultValue = "SOL")
    @JsonProperty(value = "crypto_type", required = true)
    private final String cryptoType;

    @JsonCreator
    public CreateAccountDtoRq(@NonNull String username, @NonNull String cryptoType) {
        this.username = username;
        this.cryptoType = cryptoType;
    }
}
