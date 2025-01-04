package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Атрибуты для смены пароля пользователя")
public class ResetPasswordDtoRq {
    @Schema(description = "Логин пользователя", defaultValue = "test123")
    @NonNull
    private final String login;
    @Schema(description = "Логин пользователя", defaultValue = "1234567890")
    @JsonProperty(value = "old_password", required = true)
    private final String oldPassword;
    @Schema(description = "Логин пользователя", defaultValue = "0987654321")
    @JsonProperty(value = "new_password", required = true)
    private final String newPassword;
}
