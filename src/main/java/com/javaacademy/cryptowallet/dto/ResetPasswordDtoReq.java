package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordDtoReq {
    @NonNull
    private final String login;
    @JsonProperty("old_password")
    private final String oldPassword;
    @JsonProperty("new_password")
    private final String newPassword;
}
