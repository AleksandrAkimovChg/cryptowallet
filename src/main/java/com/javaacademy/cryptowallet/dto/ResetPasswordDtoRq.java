package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

@Data
@Schema(description = "Атрибуты запроса для смены пароля пользователя")
public class ResetPasswordDtoRq {
    @Schema(description = "Логин пользователя", defaultValue = "test123")
    private final String login;
    @Schema(description = "Логин пользователя", defaultValue = "1234567890")
    @JsonProperty(value = "old_password", required = true)
    private final String oldPassword;
    @Schema(description = "Логин пользователя", defaultValue = "0987654321")
    @JsonProperty(value = "new_password", required = true)
    private final String newPassword;

    @JsonCreator
    public ResetPasswordDtoRq(@NonNull String login, @NonNull String oldPassword, @NonNull String newPassword) {
        this.login = login;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
