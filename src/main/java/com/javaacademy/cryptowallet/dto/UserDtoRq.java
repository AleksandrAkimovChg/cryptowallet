package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Атрибуты запроса для создания пользователя")
public class UserDtoRq {
    @Schema(description = "Логин пользователя", defaultValue = "test123")
    @NonNull
    private String login;
    @Schema(description = "email пользователя", defaultValue = "test@example.com")
    @NonNull
    private String email;
    @Schema(description = "пароль пользователя", defaultValue = "1234567890")
    @NonNull
    private String password;
}
