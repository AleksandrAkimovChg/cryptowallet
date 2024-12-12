package com.javaacademy.cryptowallet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDtoReq {
    @NonNull
    private String login;
    @NonNull
    private String email;
    @NonNull
    private String password;
}
